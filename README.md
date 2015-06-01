# ZooKeeperWrapper
In my project about distribute data process ,I use the original zookeeper client provided by ZooKeeper offical.That is so terrible that the client always lost connection and session expired.To slove these question, I must restart my application,sometime even the app server.I don't know the reason that ZooKeeper team don't provide a rich client.
Then I found a open source project called Curator.It is a wrapper of zookeeper and provide many tool that extend zookeeper,which is so great and amazing to me.
To use this project better, I read it's code.After reading the code of Curator,it's too complex to me I think and what I need is just a simple client with a simple gurrantee to the exception,that's all enough.There is no choice but to build by myself.If you met same problem,this will be a good choice. 
# introduce
What's your needs?If you need a sample client can deal with  exception thrown by Zookeeper client and usage is same to the offical client.
The Wrapper has same interface as ZooKeeper,just add a retry loop and exception dispose.Use the Wrapper to replace the official Zookeeper client and two client's usage are same,even you can through the factory of Zookeeper client in your project generate a wrapper and replace the official client directly which is work and support greatly.
