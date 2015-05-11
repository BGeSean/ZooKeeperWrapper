# ZooKeeperWrapper
In my project,I use the original zookeeper client provided by ZooKeeper offical.That is so terrible that I must restart my application
when session expird or some other exceptions happen.I don't understand the reason that ZooKeeper team don't provide a rich client.
Then I found a open project called Curator.It is a wrapper of zookeeper and provide many tool that extend zookeeper,which is so great
So I read it's code.Too complex I think,and what I need is a simple client with a simple gurrantee to the exception,that's all.There is 
no choice but to build by myself.If you met same problem,this will be a good choice. 
# introduce
The Wrapper has same interface to ZooKeeper,just add a retry loop and exception dispose
