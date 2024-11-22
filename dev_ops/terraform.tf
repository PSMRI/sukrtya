#This section of the Terraform configuration file defines the Terraform provider 
#configurations, as well as the required version of Terraform itself.
#============================================================================================
terraform {

  # cloud {
  #   workspaces {
  #     name = "SUkrtya-terraform-eks"
  #   }
  # }

#===========required_providers===================================
#The required_providers block specifies the providers that Terraform will use for your infrastructure. Providers allow Terraform to interact with different cloud platforms, services, or APIs to create, manage, and configure resources.
#=================================================================
#This specifies the AWS provider, which is required to interact with Amazon Web Services.
#The source field points to the HashiCorp AWS provider, 
#and the version constraint (~> 5.47.0) ensures that Terraform will use a version of the AWS provider that is compatible with version 5.47.0 
#(meaning any version from 5.47.0 up to but not including 6.0.0).
#The random provider, which is used to generate random values like strings or numbers. This is commonly used for creating unique names or resource identifiers.
#The version constraint (~> 3.6.1) ensures that the version is compatible with 3.6.1.
#he TLS provider, which is used for generating TLS certificates and managing secure communication. This can be useful for managing SSL certificates or keys within your Terraform-managed infrastructure.
#The version constraint (~> 4.0.5) ensures compatibility with version 4.0.5 of the tls provider.
#the CloudInit provider, which is used for configuring and initializing cloud instances (for example, setting up server configurations or user data on cloud instances when they are first created).
#The version constraint (~> 2.3.4) ensures compatibility with version 2.3.4 of the cloudinit provider.
#=========================================================================
  required_providers {
    aws = {
      source  = "hashicorp/aws"
      version = "~> 5.47.0"
    }

    random = {
      source  = "hashicorp/random"
      version = "~> 3.6.1"
    }

    tls = {
      source  = "hashicorp/tls"
      version = "~> 4.0.5"
    }

    cloudinit = {
      source  = "hashicorp/cloudinit"
      version = "~> 2.3.4"
    }
  }

  required_version = "~> 1.3"
}


#Summary of What This Configuration Does:
#Specifies Required Providers: The terraform block sets up the required providers for your project, including:
#aws for interacting with AWS resources.
#random for generating random values (like strings).
#tls for managing TLS certificates and secure communication.
#cloudinit for configuring cloud instances using CloudInit.
#Sets Terraform Version: It ensures that Terraform version 1.3 or compatible versions are used for applying the configuration.
#Workspace Configuration (Commented Out): There is a commented-out section for configuring a Terraform Cloud workspace, which is not currently active.
#This configuration sets up the foundational requirements for managing cloud infrastructure using Terraform, specifically targeting AWS, with additional providers for randomness, TLS certificates, and cloud instance initialization.
