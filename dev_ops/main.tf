
# 
#=========*****AWS Provider Configuration***************========================================
#This block configures the AWS provider with the specified region (provided by the variable var.region).
#===================================================================================
provider "aws" {
  region = var.region
}
#=================================================================================
#===============**** AWS Availability Zones Data***================================
# with managed node groups
#This block fetches a list of available AWS availability zones that are not restricted by opt-in status.
# It's used later to determine where to place resources like subnets.
#============================================================================
data "aws_availability_zones" "available" {
  filter {
    name   = "opt-in-status"
    values = ["opt-in-not-required"]
  }
}
#============*****Cluster Name Generation*******==========================================
#This block creates a local variable called cluster_name for the EKS cluster,
# appending a randomly generated string (random_string.suffix.result) to the base name SUkrtya-eks.
#==========================================================================================
locals {
  cluster_name = "SUkrtya-eks-${random_string.suffix.result}"
}
#====******Random String Resource*******=======================================================
#This resource generates a random string (8 characters long) that is appended to the cluster name to ensure uniqueness.
#===============================================================================
resource "random_string" "suffix" {
  length  = 8
  special = false
}
#=============******VPC Creation*******=========================================================
#This block creates a VPC (Virtual Private Cloud) in AWS with a CIDR block of 10.0.0.0/16 and divides it into public and private subnets.
#It uses 3 availability zones (obtained from the previous data block) and enables NAT gateways for private subnets to access the internet.
#The VPC is configured with DNS hostnames, and specific tags are added to the subnets for Kubernetes roles.
#=================================================================================
module "vpc" {
  source  = "terraform-aws-modules/vpc/aws"
  version = "5.8.1"

  name = "SUkrtya-vpc"

  cidr = "10.0.0.0/16"
  azs  = slice(data.aws_availability_zones.available.names, 0, 3)

  private_subnets = ["10.0.1.0/24", "10.0.2.0/24", "10.0.3.0/24"]
  public_subnets  = ["10.0.4.0/24", "10.0.5.0/24", "10.0.6.0/24"]

  enable_nat_gateway   = true
  single_nat_gateway   = true
  enable_dns_hostnames = true

  public_subnet_tags = {
    "kubernetes.io/role/elb" = 1
  }

  private_subnet_tags = {
    "kubernetes.io/role/internal-elb" = 1
  }
}
#===========EKS Cluster Creation================================================================
#This block creates the EKS (Elastic Kubernetes Service) cluster.
#The cluster is named based on the earlier generated local variable cluster_name.
#The EKS version is set to 1.29, and public access to the cluster endpoint is enabled.
#IAM roles and permissions for EBS CSI (Elastic Block Store Container Storage Interface) are configured as cluster add-ons.
#The cluster is set up in the private subnets of the created VPC.
#Two managed node groups are created with t3.small instances, one with 2 desired nodes and the other with 1.
#===========================================================
module "eks" {
  source  = "terraform-aws-modules/eks/aws"
  version = "20.8.5"

  cluster_name    = local.cluster_name
  cluster_version = "1.29"

  cluster_endpoint_public_access           = true
  enable_cluster_creator_admin_permissions = true

  cluster_addons = {
    aws-ebs-csi-driver = {
      service_account_role_arn = module.irsa-ebs-csi.iam_role_arn
    }
  }

  vpc_id     = module.vpc.vpc_id
  subnet_ids = module.vpc.private_subnets

  eks_managed_node_group_defaults = {
    ami_type = "AL2_x86_64"

  }

  eks_managed_node_groups = {
    one = {
      name = "node-group-1"

      instance_types = ["t3.small"]

      min_size     = 1
      max_size     = 3
      desired_size = 2
    }

    two = {
      name = "node-group-2"

      instance_types = ["t3.small"]

      min_size     = 1
      max_size     = 2
      desired_size = 1
    }
  }
}


#==========**** IAM Policy for EBS CSI Driver***=================================
#This data block fetches the IAM policy (AmazonEBSCSIDriverPolicy) that is required by the Amazon 
#EBS CSI driver for managing persistent storage in EKS.
#====================================================================================
data "aws_iam_policy" "ebs_csi_policy" {
  arn = "arn:aws:iam::aws:policy/service-role/AmazonEBSCSIDriverPolicy"
}

#=======*****IRSA Role for EBS CSI Driver****============================
#This module creates an IAM Role that the EBS CSI driver will use to interact with AWS services (specifically for EBS volume operations). 
#It utilizes IRSA (IAM Roles for Service Accounts), which integrates with Kubernetes to assign AWS permissions to Kubernetes service accounts.
#The role is created using the OIDC provider from the EKS cluster.
#The role is granted the AmazonEBSCSIDriverPolicy to manage EBS volumes within the EKS cluster.
#==========================================================================================
module "irsa-ebs-csi" {
  source  = "terraform-aws-modules/iam/aws//modules/iam-assumable-role-with-oidc"
  version = "5.39.0"

  create_role                   = true
  role_name                     = "AmazonEKSTFEBSCSIRole-${module.eks.cluster_name}"
  provider_url                  = module.eks.oidc_provider
  role_policy_arns              = [data.aws_iam_policy.ebs_csi_policy.arn]
  oidc_fully_qualified_subjects = ["system:serviceaccount:kube-system:ebs-csi-controller-sa"]
}
#==========*****End********==================================================================================