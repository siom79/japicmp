#!/usr/bin/python

import sys, getopt
import subprocess
import os
import fileinput


def main(argv):
	(gpgpassphrase, dryRun, devVersion, releaseVersion, releaseTag, oldVersion) = parseCli(argv)
	replaceOldVersion(oldVersion, releaseVersion, dryRun)
	releaseBuild(gpgpassphrase, dryRun, devVersion, releaseVersion, releaseTag)


def parseCli(argv):
	gpgpassphrase = None
	dryRun = False
	devVersion = None
	releaseVersion = None
	releaseTag = None
	oldVersion = None
	try:
		opts, args = getopt.getopt(argv, "hg:dn:r:t:o:", ["gpg-passphrase=", "dry-run=", "new-version=", "release-version=", "release-tag=", "old-version="])
	except getopt.GetoptError:
		printHelp()
		sys.exit(2)
	for opt, arg in opts:
		if opt == '-h':
			printHelp()
			sys.exit()
		elif opt in ("-g", "--gpg-passphrase"):
			gpgpassphrase = arg
		elif opt in ("-d", "--dry-run"):
			dryRun = True
		elif opt in ("-n", "--new-version"):
			devVersion = arg
		elif opt in ("-r", "--release-version"):
			releaseVersion = arg
		elif opt in ("-t", "--release-tag"):
			releaseTag = arg
		elif opt in ("-o", "--old-version"):
			oldVersion = arg
	if gpgpassphrase == None:
		print("Missing option --gpg-passphrase:")
		printHelp()
		sys.exit(2)
	if devVersion == None:
		print("Missing option --new-version:")
		printHelp()
		sys.exit(2)
	if releaseVersion == None:
		print("Missing option --release-version:")
		printHelp()
		sys.exit(2)
	if releaseTag == None:
		print("Missing option --release-tag:")
		printHelp()
		sys.exit(2)
	if oldVersion == None:
		print("Missing option --old-version:")
		printHelp()
		sys.exit(2)
	return (gpgpassphrase, dryRun, devVersion, releaseVersion, releaseTag, oldVersion)


def replaceOldVersion(oldVersion, releaseVersion, dryRun):
	for dirpath, dnames, fnames in os.walk("./"):
		for f in fnames:
			if f.endswith(".md") and not f.startswith("ReleaseNotes"):
				filedata = None
				with open(os.path.join(dirpath, f), 'r') as file:
					filedata = file.read()
				filedata = filedata.replace(oldVersion, releaseVersion)
				with open(os.path.join(dirpath, f), 'w') as file:
					file.write(filedata)
				print("Replaced " + oldVersion + " with " + releaseVersion + " in file " + os.path.join(dirpath, f) + ".")
	args = ["git", "commit", "-a", "-m", "upgraded version in *.md files to " + releaseVersion]
	print("Commiting changes: " + ' '.join(args))
	if not dryRun:
		returncode = subprocess.call(args)
		if returncode != 0:
			print("Commit failed with error code " + str(returncode) + ".")


def releaseBuild(gpgpassphrase, dryRun, devVersion, releaseVersion, releaseTag):
	args = ["mvn",
			"release:clean",
			"release:prepare",
			"-DautoVersionSubmodules=true",
			"-Dgpg.passphrase=" + gpgpassphrase,
			"-DdryRun=" + ("true" if dryRun else "false"),
			"-DdevelopmentVersion=" + devVersion,
			"-DreleaseVersion=" + releaseVersion,
			"-Dtag=" + releaseTag]
	print("Executing: " + ' '.join(args))
	returncode = subprocess.call(args)
	if returncode != 0:
		print("Release prepare failed with error code " + str(returncode) + ".")
		sys.exit(2)
	args = ["mvn",
			"release:perform",
			"-Dgpg.passphrase=" + gpgpassphrase,
			"-DdryRun=" + ("true" if dryRun else "false")]
	print("Executing: " + ' '.join(args))
	returncode = subprocess.call(args)
	if returncode != 0:
		print("Release prepare failed with error code " + str(returncode) + ".")
		sys.exit(2)


def printHelp():
	print("release.py --gpg-passphrase <gpgpassphrase> --dry-run --new-version <new-version> --release-version <release-version> --release-tag <release-tag> --old-version <old-version>")


if __name__ == "__main__":
	main(sys.argv[1:])
