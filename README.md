#japicmp#

japicmp is a tool to compare two versions of a jar archive.

The official website is located at [https://siom79.github.io/japicmp](https://siom79.github.io/japicmp/).

##Downloads##

You can download the latest version from the [release page](https://github.com/siom79/japicmp/releases) or directly from the [maven central repository](http://search.maven.org/#search%7Cga%7C1%7Ca%3A%22japicmp%22).

#Development#

* ![Build Status](https://travis-ci.org/siom79/japicmp.svg?branch=development)

##Reports##

Use the maven site plugin (`mvn site`) to generate the following reports:
 * findbugs
 * checkstyle
 * japicmp
 * cobertura test coverage

##Release##

This is the release procedure:
* Increment version in README.md
* Run release build (substitute passphrase with your GPG password):
```
mvn release:clean release:prepare -DautoVersionSubmodules=true -Dgpg.passphrase=passphrase
mvn release:perform -Dgpg.passphrase=passphrase
```
* Login to [Sonatype's Nexus repository](https://oss.sonatype.org/)
	* Download released artifact from staging repository.
	* Close and release staging repository if sanity checks are successful.

##Contributions##

Pull requests are welcome, but please follow these rules:

* The basic editor settings (indentation, newline, etc.) are described in the `.editorconfig` file (see [EditorConfig](http://editorconfig.org/)).
* Provide a unit test for every change.
* Name classes/methods/fields expressively.
* Fork the repo and create a pull request (see [GitHub Flow](https://guides.github.com/introduction/flow/index.html)).
