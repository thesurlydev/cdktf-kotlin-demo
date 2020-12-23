package io.futz.cdktf

import com.hashicorp.cdktf.App
import com.hashicorp.cdktf.TerraformOutput
import com.hashicorp.cdktf.TerraformOutputConfig
import com.hashicorp.cdktf.TerraformStack
import imports.aws.AwsProvider
import imports.aws.DataAwsCallerIdentity
import imports.terraform_aws_modules.eks.aws.Eks
import imports.terraform_aws_modules.eks.aws.EksOptions
import imports.terraform_aws_modules.vpc.aws.Vpc
import imports.terraform_aws_modules.vpc.aws.VpcOptions
import software.constructs.Construct

class EksStack(scope: Construct, id: String) : TerraformStack(scope, id) {
  init {
    AwsProvider.Builder.create(this, "aws").region("us-west-2").build()

    val vpcOptions = VpcOptions.builder()
      .name("cdktf-demo")
      .cidr("10.0.0.0/16")
      .azs(listOf("us-west-2a", "us-west-2b", "us-west-2c"))
      .privateSubnets(listOf("10.0.1.0/24", "10.0.2.0/24", "10.0.3.0/24"))
      .publicSubnets(listOf("10.0.101.0/24", "10.0.102.0/24", "10.0.103.0/24"))
      .enableNatGateway(true)
      .build()

    val vpc = Vpc(this, "vpc", vpcOptions)

    val eksOptions = EksOptions.builder()
      .clusterName("cdktf-demo")
      .subnets(vpc.privateSubnets)
      .vpcId(vpc.vpcIdOutput)
      .manageAwsAuth("false")
      .build()

    val eks = Eks(this, "eks", eksOptions)

    val clusterEndpoint = TerraformOutputConfig.builder().value(eks.clusterEndpointOutput).build()
    TerraformOutput(this, "cluster-endpoint", clusterEndpoint)

    val createUserArn = TerraformOutputConfig.builder().value(DataAwsCallerIdentity(this, "current").arn).build()
    TerraformOutput(this, "create-user-arn", createUserArn)
  }
}

fun main() {
  val app = App()
  EksStack(app, "cdktf-kotlin-demo")
  app.synth()
}
