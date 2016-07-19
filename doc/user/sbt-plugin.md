[SBT-POM-Meta]: http://www.scala-sbt.org/1.0/docs/Using-Sonatype.html#Third+-+POM+Metadata
[SBT-Credentials]: http://www.scala-sbt.org/0.13/docs/Publishing.html#Credentials

## How to publish on scaladex

The fastest way to publish your artifact to Scaladex is using the Scaladex SBT plugin. The publish workflow is
pretty much the same as you might know already. One difference is, that Scaladex plugin will only publish
the POM file. Your packages, docs and sources are not send to Scaladex.  If you don't want to use the plugin, you can
just wait until Scaladex reindex itself. This process could maybe take some days so please be patient.

For Scaladex is necessary to have a SCM tag in the Pom file. SCM means source control management. It's also required at the moment that you have your source code published on [GitHub].
To add an SCM tag to your project you can follow the guidelines of SBT.
[SBT Documentation - POM Metadata][SBT-POM-Meta]

### Install the plugin

To get the Scaladex plugin, you only need to add `addSbtPlugin("ch.epfl.scala.index" % "sbt-scaladex" % "0.1.3")` to
your `plugin.sbt` file.

### Publish Credentials

To publish to Scaladex you need to add some user authentication. Scaladex uses [GitHub] to authenticate your publish
process and verify that you have push permission to the defined repository (SCM Tag). You can see
[SBT Credentials documentation][SBT-Credentials] for how to add the login credentials.

### Configure the publish process

There are some settings for the Plugin to control the output on Scaladex a bit, like adding keywords, show [GitHub] info,
show [GitHub] Readme file, show [GitHub] contributors.

* **scaladexBaseUrl**: This is the main url to publish to _default_: `https://index.scala-lang.org`
* **scaladexKeywords**: List of keywords for your artifact _default_: `empty`
* **scaladexDownloadReadme**: A flag to download the README from [GitHub]. _default_: `true`
* **scaladexDownloadInfo**: A flag to download the repository info from [GitHub] (eg: stars, forks, ...). _default_: `true`
* **scaladexDownloadContributors**: A flag to download the contributors info from [GitHub]. _default_: `true`
* **[Credentials][SBT-Credentials] (SBT default)**: Configuration for your [GitHub] credentials to verify write access to the SCM Tag in the Pom file.

**SBT Simple Example**
```scala
scaladexKeywords in Scaladex := Seq("Foo", "Bar", "Baz")
credentials in Scaladex += Credentials(Path.userHome / ".ivy2" / ".scaladex.credentials")
/*
realm=Scaladex Realm
host=localhost
user=<github username>
password=<github password>
*/

// or Credentials("Scaladex Realm", "localhost", "<github username>", "<github password>")
```

**Disable Readme, Info, or Contributors**

This might be important for private repositories. With the Plugin we're able to index private repositories
and fetch Contributors, Readme and the Repository Info. If there is critical info configure the access.

```scala
scaladexDownloadReadme in Scaladex := false
scaladexDownloadInfo in Scaladex := false
scaladexDownloadContributors in Scaladex := false
```
### Response codes / Messages

Maybe, you get an error during publishing. This explanation will help you to solve problems you might have.

* **Forbidden** - You don't have push permission to the [GitHub] repository.
* **NoContent** - The SCM Tag in POM file is Missing. See [SBT Documentation][SBT-POM-Metadata] for how to solve.
* **Unauthorized** - Your login credentials are not right. Check your configuration if you provide 
credentials and also check if they're correct.
* **destination file exists and overwrite == false** - The version exists already on Scaladex. Overriding is
only allowed for Snapshots.

### Run Publish process

The command to publish your artifact to Scaladex is straight forward. If you have the configuration done
(don't forget to call `reload`) you can simply trigger the SBT-task `scaladex:publish`. If you have trouble
doing this, have a look at the **Response codes / Messages** section. 

```bash
sbt scaladex:publish
```