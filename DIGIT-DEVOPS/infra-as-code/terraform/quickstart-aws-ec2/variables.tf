variable "region" {
  default = "ap-south-1"
}

variable "ami_name_value" {
  default = "ami-0bb9e2d19522c61d4"
}

variable "instance_type" {
  default = "c5.2xlarge"
}

## change tag name eg. digit-quickstart_your-name

variable "tag" {
    default = "digit-quickstart"
}

## change ssh public_key with your public ssh key

variable "public_key" {
  default = "ssh-ed25519 AAAAC3NzaC1lZDI1NTE5AAAAIKD50lsy02+k/AbxxS7IslF+VdlnU3YQ9qacPv88C4IY manish_k@Manish" 
  description = "ssh key"
}

## change ssh key_name eg. digit-quickstart_your-name

variable "key_name" {
  default = "digit-quickstart"  
  description = "ssh key name"
}
