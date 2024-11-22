
#This Terraform script defines output variables for your infrastructure,
# specifically related to the Amazon EKS (Elastic Kubernetes Service) cluster that
# has been created in the previous sections of your script. 
#Outputs in Terraform are used to display or export specific 
#information about the resources you've created or configured, so you can easily access these values after the Terraform apply process is complete.
#=========Output cluster_endpoint=============
#Output cluster_endpoint
# It pulls the EKS cluster endpoint from the module.eks.cluster_endpoint. This endpoint is the URL that allows you to interact with the Kubernetes control plane for your EKS cluster.
# After applying this Terraform configuration, you'll be able to use this endpoint to configure kubectl or other tools to interact with the Kubernetes API server.
#===================================================
output "cluster_endpoint" {
  description = "Endpoint for EKS control plane"
  value       = module.eks.cluster_endpoint
}

#============Output cluster_security_group_id============================
#This output returns the Security Group IDs attached to the EKS cluster's control plane.
# It uses module.eks.cluster_security_group_id to get the security group(s) that are associated with the EKS control plane.
#Security groups are important for controlling inbound and outbound network traffic to your EKS control plane. This output allows you to retrieve those security group IDs if you need to update security rules or reference them in other infrastructure.
#============================================================================
output "cluster_security_group_id" {
  description = "Security group ids attached to the cluster control plane"
  value       = module.eks.cluster_security_group_id
}
#===============Output region================================
#This output will return the AWS region where your EKS cluster and other resources have been deployed.
#It uses the value of var.region—the region specified in the input variable (which you would pass in during the Terraform execution or from a .tfvars file).
#This is useful to confirm the region in which the infrastructure was created or for use in subsequent operations if needed.
#============================================================

output "region" {
  description = "AWS region"
  value       = var.region
}

#=======================Output cluster_name======================================
#This output returns the name of the EKS Kubernetes cluster.
# It uses the module.eks.cluster_name, which is the cluster name that was either generated dynamically or explicitly defined in the module.eks block.
#This output is helpful if you need to reference the cluster name in other scripts or manually for administrative purposes. It's also useful when configuring tools like kubectl or for monitoring.
#=======================================================================================
output "cluster_name" {
  description = "Kubernetes Cluster Name"
  value       = module.eks.cluster_name
}

#==================End======================================================================
#Summary:
#This Terraform script defines four outputs:

#cluster_endpoint: The URL endpoint for the EKS cluster’s control plane.
#cluster_security_group_id: The security group ID(s) associated with the EKS control plane.
#region: The AWS region where the resources are deployed.
#cluster_name: The name of the EKS Kubernetes cluster.
#These outputs provide important information about the infrastructure you just created and are intended to be accessed after the Terraform apply process. You can use this information to interact with your Kubernetes cluster, manage network access, and configure various tools or services that need to reference the cluster.