variable "region" {
  description = "AWS region"
  type        = string
  default     = "ap-south-1"
}

variable "cluster_name" {
    description = "EKS cluster name"
      type        = string
      default     = "Sukrtya-eks-cluster-Nilesh"
}

variable "vpc_name" {
    description = "VPC name"
      type        = string
      default     = "Sukrtya-vpc-Nilesh"
}

variable "ami_type" {
    description   = "AWS AMI type"
      type        = string
      default     = "AL2_ARM_64" #"AL2_x86_64"
}

variable "node_group_name" {
    description = "eks managed node group name"
      type        = string
      default     = "Sukrtya-node-group"
}

variable "instance_type" {
    description = "underlying ec2 instance type for the cluster"
      type        = string
      default     = "a1.medium"
}