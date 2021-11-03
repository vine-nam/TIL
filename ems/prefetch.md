receiver가 한 번에 EMS queue에서 가져오는 메시지 수를 제한한다. (default: 5)

> This property used to limit the messages fetched from EMS queue by the receiver at a time. The consumer will not be able to fetch more than the number of messages specified by prefetch.
> 
> 이 속성은 수신자가 한 번에 EMS 대기열에서 가져온 메시지를 제한하는 데 사용됩니다. 소비자는 프리페치로 지정된 메시지 수 이상을 가져올 수 없습니다.
> 
> 출처: <https://techieswiki.com/tibco-ems-tutorial-destination-properties-for-queues-and-topics.html> 



```
tcp://localhost:8222> create queue DBLOG
Queue 'DBLOG' has been created (promoted from dynamic)

tcp://localhost:8222> setprop queue DBLOG prefetch=1
Queue 'DBLOG' has been modified

tcp://localhost:8222> show queue DBLOG
 Queue:                 DBLOG
 Type:                  static
 Properties:            prefetch=1,*store=$sys.nonfailsafe
 JNDI Names:            <none>
 Bridges:               <none>
 Receivers:             2
 Pending Msgs:          0, (0 persistent)
 Delivered Msgs:        0
 Pending Msgs Size:     0.0 Kb, (0.0 Kb persistent)
```
