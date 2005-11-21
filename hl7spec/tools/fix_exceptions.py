# De-CORBAize exception classes

import sys
import re
import os

if len(sys.argv) < 3:
	print( "Usage: " + sys.argv[0]  + " <in directory> <out directory>")
	sys.exit()
#
# Regular expression to remove the class extension
repExtension=re.compile(r'(public\s+final\s+class\s+.*\s+extends\s+)org.omg.CORBA.UserException')
rep=r'\1java.lang.Exception'

def convert(infile, outfile):

	# Read the entire input file in in one swoop
	inf = open(infile)
	inDat=inf.read()
	inf.close()
	if(repExtension.search(inDat)):
		print "Adjusting exception:", infile
		outf = open(outfile, 'w')

		# Change the inheritence hierarchy
		inDat=repExtension.sub(rep, inDat, 1)

		# Strip the super calls
		inDat=re.sub('(super\(.*Helper.id\(\).*;)',r'// \1',inDat)

		outf.write(inDat)
		outf.close()

files=os.listdir(sys.argv[1])
for f in files:
	if re.match(r'.*\.java', f):
		convert(sys.argv[1] + os.sep + f, sys.argv[2] + os.sep + f)
                                                               
