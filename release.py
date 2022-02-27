#!/usr/bin/python

import sys, getopt
import subprocess
import os
import fileinput


def main(argv):
	(releaseVersion, oldVersion) = parseCli(argv)
	replaceOldVersion(oldVersion, releaseVersion)


def parseCli(argv):
	releaseVersion = None
	oldVersion = None
	try:
		opts, args = getopt.getopt(argv, "r:o:", ["release-version=", "old-version="])
	except getopt.GetoptError:
		printHelp()
		sys.exit(2)
	for opt, arg in opts:
		if opt == '-h':
			printHelp()
			sys.exit()
		elif opt in ("-r", "--release-version"):
			releaseVersion = arg
		elif opt in ("-o", "--old-version"):
			oldVersion = arg
	if releaseVersion == None:
		print("Missing option --release-version:")
		printHelp()
		sys.exit(2)
	if oldVersion == None:
		print("Missing option --old-version:")
		printHelp()
		sys.exit(2)
	return (releaseVersion, oldVersion)


def replaceOldVersion(oldVersion, releaseVersion):
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
	returncode = subprocess.call(args)
	if returncode != 0:
		print("Commit failed with error code " + str(returncode) + ".")


def printHelp():
	print("release.py --release-version <release-version> --old-version <old-version>")


if __name__ == "__main__":
	main(sys.argv[1:])
