FROM openjdk:11

# Install wget and Hadoop
RUN apt-get update && apt-get install -y wget && \
    wget https://downloads.apache.org/hadoop/common/hadoop-3.3.6/hadoop-3.3.6.tar.gz && \
    tar -xvzf hadoop-3.3.6.tar.gz && \
    mv hadoop-3.3.6 /opt/hadoop && \
    rm hadoop-3.3.6.tar.gz

# Set environment variables
ENV HADOOP_HOME=/opt/hadoop
ENV PATH=$HADOOP_HOME/bin:$PATH
ENV HADOOP_CLASSPATH="$($HADOOP_HOME/bin/hadoop classpath)"

# Copy all source code
WORKDIR /usr/src/app
COPY . .

# Compile during container run, not build
CMD javac -cp "$($HADOOP_HOME/bin/hadoop classpath)" main/Main.java tracker/*.java controller/*.java predictor/*.java evaluator/*.java && \
    java -cp ".:$($HADOOP_HOME/bin/hadoop classpath)" main.Main
