import sbt._

object AppDependencies {

  private val bootstrapVersion = "8.4.0"
  private val hmrcMongoVersion = "1.7.0"


  val compile = Seq(
    play.sbt.PlayImport.ws,
    "uk.gov.hmrc"                   %% "play-frontend-hmrc-play-30"     % "8.2.0",
    "uk.gov.hmrc"                   %% "bootstrap-frontend-play-30"     % bootstrapVersion,
    "uk.gov.hmrc.mongo"             %% "hmrc-mongo-play-30"             % hmrcMongoVersion,
    "uk.gov.hmrc"                   %% "domain-play-30"                 % "9.0.0",
    "org.typelevel"                 %% "cats-core"                      % "2.3.0",
    "org.apache.xmlgraphics"        %  "fop"                            % "2.7"
  )

  val test = Seq(
    "uk.gov.hmrc"             %% "bootstrap-test-play-30"  % bootstrapVersion,
    "uk.gov.hmrc.mongo"       %% "hmrc-mongo-test-play-30" % hmrcMongoVersion,
    "org.scalatestplus"       %% "scalacheck-1-17"         % "3.2.17.0"
  ).map(_ % Test)

  val integration: Seq[ModuleID] = Seq.empty

  def apply(): Seq[ModuleID] = compile ++ test
}
