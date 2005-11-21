# De-CORBAize exception classes

import sys
import re
import os

if len(sys.argv) < 2:
	print( "Usage: " + sys.argv[0]  + " <directory>")
	sys.exit()

files=os.listdir(sys.argv[1])
for f in files:
	if re.match(r'.*Operations\.java', f):
		f = re.sub(r'(.*)Operations\.java',r'\1.java' , f)
		print "Deleting: ", f
		os.remove(sys.argv[1] + os.sep + f);

