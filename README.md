#claimintel

This project is an attempt to build a custom Business Intelligence (BI) tool around Medicare Claims Data for the years 2008-2010, made available by the [Centers for Medicare and Medicaid Services (CMS)](http://www.cms.gov/) in anonymized form. The data is for 2,326,856 unique members, consisting of 1,332,822 inpatient and 15,826,987 outpatient records. The tool is meant to help human analysts find interesting subgroups (cohorts) from this population for further analysis.

See my [blog post](http://sujitpal.blogspot.com/2014/11/more-scala-web-development-with-spring.html) for some interesting screenshots, it may spark some ideas about what you can do with this tool.


##Architecture

The project is a [Spring](http://projects.spring.io/spring-framework/) based web application written mostly in [Scala](http://www.scala-lang.org/). The view layer uses [JSTL](https://jstl.java.net/) which can only recognize [Java](https://www.java.com/en/) objects and collections, so the Scala code populates Java Data Transfer Objects (DTOs) which are referenced from within the JSTL.

The data is stored in [Solr](http://lucene.apache.org/solr/) and retrieved by the Scala code using the [SolrJ](http://wiki.apache.org/solr/Solrj) API. 

Since a big part of effective BI is visualization, there are lots of charts that are built dynamically using the [JFreeChart](http://www.jfree.org/jfreechart/) library.

One of my [older blog posts](http://sujitpal.blogspot.com/2014/11/scala-web-development-with-spring-and.html) covers some of the other motivations for using these particular components.


##Installation Instructions

###Get the Data

The data is available from the CMS Site [here](http://www.cms.gov/Research-Statistics-Data-and-Systems/Downloadable-Public-Use-Files/SynPUFs/index.html). The data is spread across multiple pages and made available as ZIP files. Organize your data under a top level directory. Assuming this directory is named $DATA\_DIR, the data should be organized as follows (the loader code expects this structure):

    $DATA_DIR
      |
      +-- benefit_summary
      |   +-- DE1_0_2008_Beneficiary_Summary_File_Sample_10.csv
      |   +-- ...
      |   +-- DE1_0_2010_Beneficiary_Summary_File_Sample_9.csv
      +-- inpatient_claims
      |   +-- DE1_0_2008_to_2010_Inpatient_Claims_Sample_10.csv
      |   +-- ...
      |   +-- DE1_0_2008_to_2010_Inpatient_Claims_Sample_9.csv
      +-- outpatient_claims
      |   +-- DE1_0_2008_to_2010_Outpatient_Claims_Sample_10.csv
      |   +-- ...
      |   +-- DE1_0_2008_to_2010_Outpatient_Claims_Sample_9.csv

###Install Solr

Download Solr if you don't already have it installed. I used Solr 4.9 (because thats what I had installed on my machine at the time). If you use a different version, make sure to change the reference in build.sbt before you compile.

Installation is just a matter of expanding the index. Solr comes with an example index which you can cannibalize for your own purposes (see below).

###Get and Build the Code

The following sequence of commands will download this project from Github and build it. Assuming your current directory is $PROJECTS\_HOME, the code will now be available at $PROJECTS\_HOME/claimintel. 

    git clone https://github.com/sujitpal/claimintel.git
    cd claimintel
    sbt clean compile

###Load Index

Remove the example index and copy the supplied schema.xml file over the one provided by Solr, then start Solr. Assuming your Solr install directory is $SOLR\_HOME, the following sequence of commands should do this.

    cd $SOLR_HOME/examples/solr/collection1/data
    rm -rf *
    cd $PROJECTS_HOME/claimintel
    cp src/main/resources/schema.xml $SOLR_HOME/examples/solr/collection1/conf/
    cd $SOLR_HOME/examples
    java -jar start.jar

You will need to change the _DataDir_ variable in DataLoader.scala to point to the top level directory where you have downloaded your data, ie, $DATA\_DIR. 

    sbt run
    Multiple main classes detected, select one to run:
       [1] com.mycompany.claimintel.loaders.MortalityUpdater
       [2] com.mycompany.claimintel.loaders.IOCountUpdater
       [3] com.mycompany.claimintel.loaders.DataLoader
    Enter number: 

Enter the number for DataLoader (3 in this case). Note that this will take a while to run, in my case it took over a day to complete this step. Once this is done, run the other two (any order is fine). Note once again that the IOCountUpdater takes a while, for me to took little under a day to complete. In comparison the MortalityUpdater is quick, it took 15-20 minutes for me.

###Run the web application

If you are using Tomcat or Jetty, you will need to increase the size of the GET URL as [described here](http://serverfault.com/questions/56691/whats-the-maximum-url-length-in-tomcat) or [here](http://serverfault.com/questions/136249/how-do-we-increase-the-maximum-allowed-http-get-query-length-in-jetty) respectively. If you are using some other container, please RTFM. Running "sbt package" will create a WAR file which you can install as a top level (ie ROOT.war) web application in your container of choice.

The claimintel application also comes with its own built-in Jetty container via sbt which is very convenient for development. You can start and stop this from the sbt console using the following commands:

    sbt container:start  # start Jetty
    sbt container:stop   # stop Jetty

Once the container (and Solr) has started, the application can be accessed via a browser at localhost:8080/index.html.

