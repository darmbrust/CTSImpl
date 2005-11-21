import sys
import re

# Process an IDL file for publication in the HL7 XML specification

def stripTab(line):
	# pull 8 spaces or a tab off of the front of a line
	if line[0] == " ":
		line = re.sub(r'[ ]{1,7}',r'',line,1)
	if line[0] == " " or line[0] == "\t":
		line = line[1:]
	return line

# clean the line up a bit for XML & other processing
# ntabs counts the number of tabs in the first line to be massaged.
# it gets reset at the beginning of each new section
ntabs=0
def massageLine(line):
	global ntabs
	
	line = stripTab(line)
	if ntabs == 0:
		if line[0] == ' ' or line[0] == '\t':
			ntabs = 2
		else:
			ntabs = 1
	if ntabs == 2:
		line = stripTab(line)
	

	# escape lt's and ampersands
	line = re.sub(r'&',r'&amp;',line)
	line = re.sub(r'<',r'&lt;',line)

	return line.rstrip() + '\n'
		

# First state - outside of a module
state=0
needLine=0
block=""
EOL="\n"


moduleHead = re.compile(r'module\s+([^{\s]*)\s*{')
specHead = re.compile(r'\s*/\*\s+HL7SpecBlock:([^\s]+)\s+\*/')
specEnd = re.compile(r'\s*/\*\s+HL7SpecBlockEnd\s+\*/')
specEndSpecial = re.compile(r'\s*/\*\s+HL7SpecBlockEndElipsis\s+\*/')
startComment = re.compile(r'/\*')
endComment = re.compile(r'\*/')

infile=open(sys.argv[1])
outfile=open(sys.argv[2],"w")
line=infile.readline()

outfile.write(r'<?xml version="1.0" encoding="UTF-8"?>' + EOL)
outfile.write(r'<idlSpec>' + EOL)
while(line):
	if needLine != 0:
		line=infile.readline()
		# outfile.write( state, "): ", line\\n,)
		needLine=0
		
	# State 0: before the module start
	if state == 0:
		if moduleHead.search(line):
			state = 1
		needLine=1
	
	# State 1: module start, outside a block
	elif state == 1:
		if specHead.search(line):
			ntabs = 0
			state = 4
			block = specHead.sub(r'\1',line).strip()
			outfile.write(r'<' + block + r'>' + EOL)
		elif startComment.match(line.strip()):
			state = 5
		else:
			sline = line.strip()
			if sline != "" and sline != "};" and sline != "#endif":
				sys.stderr.write("Information found outside of a specification block\\n")
				sys.stderr.write("   ..." + line)
		needLine=1

	# State 2: inside a block
	elif state == 2:
		if specEnd.search(line):
			state = 1
			outfile.write(r'</' + block + r'>' + EOL)
			needLine=1
		elif specEndSpecial.search(line):
			state = 1
			outfile.write(r'                        ...' + EOL)
			outfile.write(r'};' + EOL)
			outfile.write(r'</' + block + r'>' + EOL)
			needLine=1
		elif startComment.match(line.strip()):
			state = 3
		elif line.strip() == "":
			outfile.write("\n")
			state = 4
		else:
			outfile.write(massageLine(line))
			needLine=1
			
	# State 3: inside a comment
	elif state == 3:
		if endComment.search(line):
			state = 4
		needLine = 1
		
	# State 4: eating white space
	elif state == 4:
		if line.strip() != "":
			state = 2
		else:
			needLine = 1
			
	# State 5: outside a block inside a comment
	else:
		if endComment.search(line):
			state = 1
		needLine = 1

outfile.write(r'</idlSpec>' + EOL)
