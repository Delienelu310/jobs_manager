# Ilum Orchestrate

Built on top of **Ilum**, which uses **Apapche Spark** for Big Data solutions.

**Ilum Orchestrate** is a **framework** aimed at organising complex data flow into **distinct stages** with clear input/output format and **automated testing**, while also **abstracting away** complex system logic from data scientists


## How does it work?
1. Data Flow Architect builds a **graph**, where each **node** corresponds to the **stage**. He specifies the **headers** of input/output data, the **type** of storage/stream that data is moving from. 
2. The data scientist uses **annotation lib** when creating a script. This way he **does not** creat spark session and **manually connect** to the data locations, but instead **uses labels** specified by the architect. 
3. Testers write their test-scripts in a similar way to data scientists. Their scripts return **json objects with custom metrics**. All the scripts and test related to single stage are pushed to the **queue** and the **ilum job is launched**, which launches each script and test and then generates **job results**, allowing
to compare and choose the best algorithm
4. Project administrator can easily **manage roles** of each user, **restricting their access** to stages, that they should not work with

## What is the main idea behind Ilum Orchestrate?

1. Data scientists should not care about other stages of the data flow, as well as about technical details of the system. The only thing they should care
about is input data, output data and the algorithms/spark sql queries they are going to use to convert one to another to achieve maximum ranking according
to metrics calculated by testers

2. The Data Flow layer should not be dependent on the storage or streaming used. If anything will be changed in the cluster, the architecture can be adapted
easily


## Quick start:

1. **Start minikube**:
``` bash
minikube start --cpus 4 --memory 8192 --addons metrics-server
```

2. **Deploy Ilum**:
```bash
helm repo add ilum https://charts.ilum.cloud
helm install ilum ilum/ilum
```

3. **Deploy ilum orchestry**
```bash
helm repo add jobs-manager https://Delienelu310.github.io/jobs_manager/jobs-manager-helm-repo/
helm isntall jobsmanager jobs-manager/jobs-manager-helm-chart-1.0.0
```

4. **Port-forward to use client**
```bash
kubectl port-forward svc/jobs-manager-client 3000:80
```
5. **Finally go to localhost:3000 in your browser**


## jobs manager helm chart configuration:
```yaml
jobs_manager:
  admin: 
    username: admin
    password: admin
    fullname: null

mongodb:
  admin:
    username: mongoadmin
    password: mongoadmin
  uri: "mongodb://mongoadmin:mongoadmin@jobs-manager-mongodb:27017"

minio:
  admin:
    username: minioadmin
    password: minioadmin
```
**Important notes:**
1. minio admin credentials should be the same as in Ilum part of cluster
2. mongon db credentials must correspond to uri

**Example of luanching helm chart with custom config**:
```
helm isntall jobsmanager jobs-manager/jobs-manager-helm-chart-1.0.0 \
    --set jobs_manager.admin.username=myadmin,jobs_manager.admin.password=mypassword
```

## Usage
1. **Build data flow architecture**

- Go to project page and use **interactive canvas**.

- Use **JobNode Mod** to create stages

- Using **Cursor Mod**, click on bars to open bar menu, where you can add input/outputs

- Use **Connect Mod** to connect inputs and outputs, while configuring the **channel headers and type**

**Video example:**


2. **Create script**

- Create java project with maven and jdk8+

- Add dependency from **Ilum**:
```xml
<dependency>
    <groupId>cloud.ilum</groupId>
    <artifactId>ilum-job-api</artifactId>
    <version>6.0.0</version>
</dependency>
```
- Add dependency from **Ilum Orchestry**:
```xml
<repositories>
    <repository>
      <id>github</id>
      <url>https://maven.pkg.github.com/Delienelu310/jobs_connection_lib</url>
    </repository>
</repositories>

<dependency>
      <groupId>com.ilumusecase</groupId>
      <artifactId>jobs_connection_lib</artifactId>
      <version>1.0</version>
</dependency>
```
- Create class that implements ```Job``` interface from **ilum-job-api**
- Use annotations ```@JobNode``` on class, ```@InputChannel``` and ```@OutputChannel``` on its static fields of type ```Dataset<Row>```
- In the method run, before main code, create ```JobProcessor``` object and **start** it.
- In your code, read data from datasets with ```@InputChannel``` and assigns output datasets to fields with ```@OutputChannel``` 
- In the end of your code, call **finish** method of ```JobProcessor```

**How it should look like:**
```java
@JobNode
public final class SoloRSI implements Job {
    @InputChannel(label = "RSI")
    static public Dataset<Row> RSI;
    @OutputChannel(label = "Signal")
    static public Dataset<Row> signals;

    @Override
    public Option<String> run(SparkSession sparkSession, scala.collection.immutable.Map<String, Object> config) {
        Map<String, Object> configJava = JavaConverters.mapAsJavaMapConverter(config).asJava();

        JobProcessor jobProcessor = new JobProcessor(SoloRSI.class, sparkSession, configJava);

        jobProcessor.start();

        //... your code

        SoloRSI.signals = outputDataset;

        jobProcessor.finish();

        return Some.apply("DONE");

    }
}
```

**For more examples look into trading_bot folder of the project**

3. **Create Script-Tester in a similar way**
- Use ```@TestJob``` instead of```@JobNode```, use ```TestJobProcessor``` instead of ```JobProcessor```, use ```@OutputChannelTestDataset``` instead of ```@OutputChannel```
- read from ```@OutputChannelTestDataset```, don`t assign dataset to it in the code
- In the end of code **return Json String** of format 
```json
{
  "metric_1" : "value_1",
  ...
  "metric_n" : "value_n"
}
```
**For more examples look into trading_bot/test-indicators folder of the project**


4. **Upload Script an add it to queue**
- Go to Project page, choose stage, scroll down to Job Node details and click **More** to navigate to Job Node page
- Choose **JobsFiles Folder** and use Upload File form to upload file
- Choose **Job Scripts Folder** and create script, specifying its **full class name**
- In Job Script menu, add jobs file as a **dependency**
- In Job Script menu add the job script to **queue**, specifying the queue type: **jobsQueue** for job scripts, **testingQueue** for tester scripts

**Video example:**

5. **Run the Job Node to Test job Scripts**
- Go to Job Node Page
- Write **ilum group** details
- Click **Start**
- Wait for a minute
- Go to **Job Resuts** and **Job Errors** folder to see the result or errors

**Video example:**

6. **Manage Roles of a project and a job node**:


## Contribution

**How to contribute?**

- **clone project code**
```bash
git clone https://github.com/Delienelu310/jobs_manager.git
cd /jobs_manager
```

- **working on client**
  1. **Deploy the cluster** as in quick start, but dont port-forward client in the end
  2. **Port-forward** jobs_manager **backend** to the localhost:
    ```bash
      kubectl port-forward svc/jobs-manager 8080:8080
    ```
  3. **To run:** 
    ```bash
      cd ./jobs_manager_client
      npm start  
    ```
- **working on backend**
    1. **Deploy the cluster** as in quick start, use **default configuration**
    2. **Remove** existing **jobs_manager backend**
      ```bash
        kubectl delete deployment jobs-manager
      ```
    3. **Build your own**
      ```bash
        cd ./jobs_manager
        
        mvn package
        
        eval $(minikube docker-env up)
        docker build -t jobs_manager .
        
        kubectl delete -f deployment.yaml
        kubectl apply -f deployment.yaml
        kubectl port-forward svc/jobs-manager 8080:8080

      ```
- **working with jobs_connection_lib**
    1. **Build the library** 
      ```bash
        cd ./jobs_connection_lib
        mvn package  
      ``` 
    2. **Move package file to the backend resources**
      ```bash
        cd ../
        mv ./jobs_connection_lib/target/jobs_connection_lib-1.0.jar ./jobs_manager/src/main/resources/jobs_connection_lib.jar
      
      ```
    3. **Build Backend as in previous step**

**Submit a pull request**

If you'd like to contribute, please fork the repository and open a pull request to the `main` branch.

**What to contribute?**
1. **Python connection lib library**
2. **New channels types**