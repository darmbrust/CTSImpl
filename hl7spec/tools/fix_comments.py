# Make official javadoc comments out of all the regular comments in the directlry

import sys
import re
import os
repre=re.compile(r'\/\*([^\*].*?)(.*?)\*\/',re.DOTALL)
reprecr=re.compile(r'\r',re.DOTALL)
if len(sys.argv) != 2:
	print( "Usage: " + sys.argv[0]  + " <directory>")
	sys.exit()
#
# Regular expression to remove the class extension

def convert(infile):

	# Read the entire input file in in one swoop
	inf = open(infile)
	inDat=inf.read()
	inf.close()

	print "Fixing comments on:", infile
	inDat=repre.sub(r'/**\1\n *<PRE>\2</PRE>\n */',inDat)
	inDat=reprecr.sub(r'',inDat)
	outf = open(infile, 'w')
	outf.write(inDat)
	outf.close()


files=os.listdir(sys.argv[1])
for f in files:
	if re.match(r'.*\.java', f):
		convert(sys.argv[1] + os.sep + f)
                                                               
