# MajorEclipsePlugin
A plugin for Eclipse integrating the Major Mutation Framework

**Assumptions**

1. Java source files are located in a source directory named &quot;src&quot;. These source files are allowed to be in packages.  
2. Test classes are located in a package named &quot;test&quot; directly under the src directory. These test classes are NOT allowed to be in subpackages or subfolders of any kind (they must be located directly under the test package). Multiple test classes are allowed to be under the test package.

**Instructions on running plugin**

1. In the Package Explorer, open up a java project and click on a java source file that you wish to mutate.  
2. Click on the Mutate button.

**Generated Files**

One folder and two files should be generated directly under the java project&#39;s directory:

	i. a folder named mutants
	ii. a CSV file named killMatrix.csv
	iii. a log file named mutants.log

i. The mutants folder contains subfolders named &quot;1&quot;, &quot;2&quot;, &quot;3&quot;, etc. Each of these subfolders contains a mutated source file containing the mutant with mutantID equal to the subfolder&#39;s number.  
ii. killMatrix.csv contains information on which mutants were killed by which tests.  
iii. mutants.log contains information on generated mutants.

**View**

A view named &quot;Mutants&quot; should open up. The view should list each mutant by their mutantID. To the left of each mutantID should be one of the following images:

	i. a green plus sign
	ii. blue circles
	iii. a red X

i. A green plus sign means that the mutant is killed.  
ii. Blue circles mean that the mutant is covered but still alive.  
iii. A red X means that the mutant is uncovered (and therefore still alive).

Buttons are located towards the top of the view that allow different sorting options for the mutants.

Perform a right-click on a mutant listed in the view to open the context menu. Three options should be displayed:

	i. Display a Mutant
	ii. Highlight Mutant in Source File
	iii. Highlight Mutant in Mutated Source File

i. The Display a Mutant option opens up a message box with a description of the mutant. The description shown is the mutant&#39;s description in the mutants.log file.  
ii. The Highlight Mutant in Source File option opens up the source file that was selected and highlights the line on which the mutant occurs. Double clicking on a mutant listed in the view also performs this option.  
iii. The Highlight Mutant in Mutated Source File option opens up the mutated source file and highlights the line on which the mutant occurs.

**Other instructions/notes**

1. Only select one file to mutate at a time.  
2. If you have mutated a file in one project and wish to mutate a file in a different project, restart Eclipse first. If you do not restart Eclipse first before doing another mutation for another project, a weird issue will happen in which the mutants folder will only get generated in the first project you did a mutation in.