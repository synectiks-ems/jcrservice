# jcrservice #

### What is this repository for? ###
A highly scalable content repository service for any base application

### How to import project for editing ###

* Import as maven project in your IDE

### Build, install and run application ###

To get started build the build the latest sources with Maven 3 and Java 8 
(or higher). 

	$ cd jcrservice
	$ mvn clean install 

You can run this application as spring-boot app by following command:

	$ mvn spring-boot:run

#### Its not running as standalone at present ####
Once done you can run the application by executing 

	$ java -jar target/jcrservice-1.7.11.jar

## Application api's documentation ##

### /oakRepo/list ###

Api to get the list of child nodes by absolute node path.

	**Method:** POST
	**Params:**
		**path**	String 	absolute node path
	**Response:**
		{}		Json string of child node objects

### /oakRepo/delete ###

 Api to get the delete a node and its sub tree by absolute node path.

	**Method:** POST
	**Params:**
		**path**	String 	absolute node path
	**Response:**
		Success

### /oakRepo/createNode ###

Api to create a new entity in jackrabbit repository at specified path. if path exists then node get created as child with new random id.

	**Method:** POST
	**Params:**
		**parentPath**	String 	node path or node parent path to create node at
		**json**		JSON 	json string with node properties to create new node
		cls				String 	specify fully qualified class name for json to type cast in
		nodeName		String 	optional specify if don't want random id as node name
	**Response:**
		{}		json of newly created object

### /oakRepo/moveNode ###

Api to move or rename node or tree from one to another path. Make sure both path must exists

	**Method:** POST
	**Params:**
		**src**		String 	source node absolute path
		**dest**	String 	destination node path
	**Response:**
		{"Result": "Success"}

### /oakRepo/similar ###

Api to find similar nodes as input node path

	**Method:** POST
	**Params:**
		**absPath**		String 	input source node absolute path
	**Response:**
		{{},{}}		JSON List of similar nodes json string

### /oakRepo/suggestions ###

Api to get suggestions for input string

	**Method:** POST
	**Params:**
		**input**	String
	**Response:**
		{, }	JSON List of suggested string

### /oakRepo/spellcheck ###

Api to find correct spelling for input string

	**Method:** POST
	**Params:**
		**input**	String
	**Response:**
		{, }		JSON List of suggested spellings

### /oakRepo/search ###

Api to search input query in jackrabbit repository.

	**Method:** POST
	**Params:**
		**path**		String 	absolute node path to search in repository.
		**cols**		String 	fields to be searched for query
		**query**		String 	string to search
		**orderBy**		String 	comma separate list of columns for result sorting.
	**Response:**
		{{},{}}		json List object of search result nodes as string

### Who do I talk to? ###
	Please mail me on
	sharmanju80@gmail.com
