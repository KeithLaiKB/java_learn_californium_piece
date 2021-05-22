package com.learn.californium.server.mydemo.myresc;

import java.net.InetSocketAddress;
import java.net.URI;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.ReentrantLock;

import org.eclipse.californium.core.CoapClient;
import org.eclipse.californium.core.CoapResource;
import org.eclipse.californium.core.coap.CoAP;
import org.eclipse.californium.core.coap.Response;
import org.eclipse.californium.core.coap.CoAP.Code;
import org.eclipse.californium.core.coap.CoAP.ResponseCode;
import org.eclipse.californium.core.coap.CoAP.Type;
import org.eclipse.californium.core.network.Endpoint;
import org.eclipse.californium.core.network.Exchange;
import org.eclipse.californium.core.observe.ObserveNotificationOrderer;
import org.eclipse.californium.core.observe.ObserveRelation;
import org.eclipse.californium.core.observe.ObserveRelationContainer;
import org.eclipse.californium.core.observe.ObserveRelationFilter;
import org.eclipse.californium.core.server.ServerMessageDeliverer;
import org.eclipse.californium.core.server.resources.CoapExchange;
import org.eclipse.californium.core.server.resources.Resource;
import org.eclipse.californium.core.server.resources.ResourceAttributes;
import org.eclipse.californium.core.server.resources.ResourceObserver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 我想要 再设置一个相同的变量 获取 现在正在连这个server的所有 client
 * 因为 不想大幅度更改原来的代码
 * 假设 我想获得 当前 变量 list_varA
 * 那我现在做一个 mylist_varA
 * 
 * 当有一个官方的方法 
 * add_varA_element(Object e){
 * 	list_varA.add(e) 
 * }
 * 
 * 那么 我就 在 这个相同的方法中
 * add_varA_element(Object e){
 * 	list_varA.add(e) 
 * 	mylist_varA.add(e) 	//添加
 * } 
 * 
 * 这样的方式 对 mylist_varA 的增删改查基本都一样
 * 使得 list_varA 和  mylist_varA 内容一致
 * 
 * 由于对 list_varA 增删改查 基本都是很简单的代码, 所以你不用管 很多其他的业务逻辑
 * 只需要对其 基本的增删改查 中 对 mylist_varA 做相同的步骤即可
 * 
 * 这种方式, 不需要改动原来的变量, 只不过增加了额外一个 变量 去同步罢了
 *  
 *  
 *  我建立这个累的初衷 则是因为
 *  想要获得连接这个resource的 所有的client 的 ip
 *  
 *  在 observeRelations 类中 
 *  	有 observers  observeRelations
 *  由于 这两个东西 的类 还有其他属性, 这些属性在 一些方法中 会被更改, 
 *  	所以很难专门设置两个变量  my_observers 或者 my_observeRelations 去同步除了IP以外的其他属性
 *  
 *  所以对于具体来说, 我就保存 Ip列表就好, 因为client的ip不容易被更改
 *  则把上述例子
 *  
 * add_varA_element(Object e){
 * 	list_varA.add(e) 
 * 	mylist_varA.add(e) 	//添加
 * } 
 * 
 * 然后改成
 * add_varA_element(Object e){
 * 	list_varA.add(e) 
 * 	mylist_varA_ip.add(e.getIp()) 	//添加, 这里则为改 e.getIp()
 * } 
 *  
 *  
 *  
 * @author laipl
 *
 */
public class MyCoapResource extends CoapResource  implements Resource {

	/** The logger. */
	protected final static Logger LOGGER = LoggerFactory.getLogger(MyCoapResource.class);



	/* The list of observers (not CoAP observer). */
	//private List<ResourceObserver> observers;

	/* The the list of CoAP observe relations. */
	//private ObserveRelationContainer observeRelations;

	
	
	//
	//
	//-------------------------------
	//
	/* 自己的 The list of observers (not CoAP observer). */
	private List<String> observers_ip;

	/* 自己的 The the list of CoAP observe relations. */
	//private List<String> observeRelations_ip;
	/** The set of observe relations */
	// 参考于 ObserveRelationContainer
	private ConcurrentHashMap<String, InetSocketAddress> observeRelations_ip = new ConcurrentHashMap<String, InetSocketAddress>();
	//
	//
	//-------------------------------
	//
	//
	//
	/**
	 * Constructs a new resource with the specified name.
	 *
	 * @param name the name
	 */
	public MyCoapResource(String name) {
		this(name, true);
	}

	/**
	 * Constructs a new resource with the specified name and makes it visible to
	 * clients if the flag is true.
	 * 
	 * @param name the name
	 * @param visible if the resource is visible
	 */
	public MyCoapResource(String name, boolean visible) {
		super(name, visible);
		/*
		this.name = name;
		this.path = "";
		this.visible = visible;
		this.attributes = new ResourceAttributes();
		this.children = new ConcurrentHashMap<String, Resource>();
		this.observers = new CopyOnWriteArrayList<ResourceObserver>();
		this.observeRelations = new ObserveRelationContainer();
		this.notificationOrderer = new ObserveNotificationOrderer();
		*/
	}
	

	





	/* (non-Javadoc)
	 * @see org.eclipse.californium.core.server.resources.Resource#add(org.eclipse.californium.core.server.resources.Resource)
	 
	 
	 * 没有更改过
	 * 感觉不需要更改
	 */
	/*
	@Override
	public synchronized void add(Resource child) {
		if (child.getName() == null) {
			throw new NullPointerException("Child must have a name");
		}
		if (child.getParent() != null) {
			child.getParent().delete(child);
		}
		children.put(child.getName(), child);
		child.setParent(this);
		for (ResourceObserver obs : observers) {
			obs.addedChild(child);
		}
	}
	*/






	/**
	 * Remove all observe relations to CoAP clients and notify them that the
	 * observe relation has been canceled.
	 * 
	 * @param code
	 *            the error code why the relation was terminated
	 *            (e.g., 4.04 after deletion)
	 *            
	 *            
	 * 没有更改过
	 * 感觉不需要更改, 因为他是要删除resource中 这个relation,而且中途还会调用 removeObserveRelation
	 * 
	 * 所以删除Ip就在 removeObserveRelation 这个方法里面操作就行
	 */
	/*
	public void clearAndNotifyObserveRelations(ResponseCode code) {
		 *
		 * draft-ietf-core-observe-08, chapter 3.2 Notification states:
		 * In the event that the resource changes in a way that would cause
		 * a normal GET request at that time to return a non-2.xx response
		 * (for example, when the resource is deleted), the server sends a
		 * notification with a matching response code and removes the client
		 * from the list of observers.
		 * This method is called, when the resource is deleted.
		 *
		for (ObserveRelation relation : observeRelations) {
			relation.cancel();
			relation.getExchange().sendResponse(new Response(code));
		}
	}
	*/

	/**
	 * Cancel all observe relations to CoAP clients.
	 * 
	 * 
	 * 没有更改过
	 * 感觉不需要更改, 因为他是要删除resource中 这个relation,而且中途还会调用 removeObserveRelation
	 * 
	 * 所以删除Ip就在 removeObserveRelation 这个方法里面操作就行
	 * 
	 */
	/*
	@Override
	public void clearObserveRelations() {
		for (ObserveRelation relation : observeRelations) {
			relation.cancel();
		}
	}
	*/





	/* (non-Javadoc)
	 * @see org.eclipse.californium.core.server.resources.Resource#addObserver(org.eclipse.californium.core.server.resources.ResourceObserver)
	 
	 *
	 * 等待更改
	 */
	@Override
	public synchronized void addObserver(ResourceObserver observer) {
		super.addObserver(observer);
		//observers.add(observer);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.californium.core.server.resources.Resource#removeObserver(org.eclipse.californium.core.server.resources.ResourceObserver)
	 
	 * 
	 * 等待更改
	 */
	@Override
	public synchronized void removeObserver(ResourceObserver observer) {
		super.removeObserver(observer);
		//observers.remove(observer);
	}









	/* (non-Javadoc)
	 * @see org.eclipse.californium.core.server.resources.Resource#setPath(java.lang.String)
	 *
	 * 没有更改过
	 * 感觉不需要更改
	 */
	/*@Override
	public synchronized void setPath(String path) {
		final String old = this.path;
		this.path = path;
		for (ResourceObserver obs : observers) {
			obs.changedPath(old);
		}
		adjustChildrenPath();
	}
	*/
	
	
	
	

	// If the parent already has a child with that name, the behavior is undefined
	/* (non-Javadoc)
	 * @see org.eclipse.californium.core.server.resources.Resource#setName(java.lang.String)
	 * 
	 * 
	 * 没有更改过
	 * 感觉不需要更改
	 */
	/*
	@Override
	public synchronized void setName(String name) {
		if (name == null) {
			throw new NullPointerException("name must not be null!");
		}
		String old = this.name;

		// adjust parent if in tree
		final Resource parent = getParent();
		if (parent!=null) {
			synchronized (parent) {
				parent.delete(this);
				this.name = name;
				parent.add(this);
			}
		} else {
			this.name = name;
		}
		adjustChildrenPath();

		for (ResourceObserver obs : observers) {
			obs.changedName(old);
		}
	}
	
	/**
	 * Adjust the path of all children. This method is invoked when the URI of
	 * this resource has changed, e.g., if its name or the name of an ancestor
	 * has changed.
	 * 
	 * 
	 * 更改过 由于, 但现在暂时不用更改
	 */
	//@Override
	//private void adjustChildrenPath() {
		//String childpath = path + name + /* since 23.7.2013 */ "/";
		// 更改
	//	String childpath = super.getPath() + super.getName() + /* since 23.7.2013 */ "/";
		//for (Resource child : children.values()) {
	//	for (Resource child : super.getChildren()) {
	//		child.setPath(childpath);
	//	}
	//}









	/* (non-Javadoc)
	 * @see org.eclipse.californium.core.server.resources.Resource#addObserveRelation(org.eclipse.californium.core.observe.ObserveRelation)
	 */
	@Override
	public void addObserveRelation(ObserveRelation relation) {
		/*
		ObserveRelation previous = observeRelations.addAndGetPrevious(relation);
		if (previous != null) {
			LOGGER.info("replacing observe relation between {} and resource {} (new {}, size {})", relation.getKey(),
					getURI(), relation.getExchange(), observeRelations.getSize());
			for (ResourceObserver obs:observers) {
				obs.removedObserveRelation(previous);
			}
		} else {
			LOGGER.info("successfully established observe relation between {} and resource {} ({}, size {})",
					relation.getKey(), getURI(), relation.getExchange(), observeRelations.getSize());
		}
		for (ResourceObserver obs:observers) {
			obs.addedObserveRelation(relation);
		}
		*/
		super.addObserveRelation(relation);
		//
		//
		//
		//
		//
		//--------------------------------
		//自己写的
		System.out.println("heyheyhey:"+relation.getSource());
		System.out.println("heyheyhey:"+relation.getKey());
		//observeRelations_ip.add(relation.)
		observeRelations_ip.put(relation.getKey(), relation.getSource());
		//--------------------------------
		//
		//
		//
	}

	/* (non-Javadoc)
	 * @see org.eclipse.californium.core.server.resources.Resource#removeObserveRelation(org.eclipse.californium.core.observe.ObserveRelation)
	 */
	@Override
	public void removeObserveRelation(ObserveRelation relation) {
		/*
		if (observeRelations.remove(relation)) {
			LOGGER.info("remove observe relation between {} and resource {} ({}, size {})", relation.getKey(), getURI(),
					relation.getExchange(), observeRelations.getSize());
			for (ResourceObserver obs : observers) {
				obs.removedObserveRelation(relation);
			}
			//
			//
			
			
		}*/
		//
		super.removeObserveRelation(relation);
		//
		//
		//
		//
		//
		//--------------------------------
		//自己写的
		observeRelations_ip.remove(relation.getKey(), relation.getSource());
		//--------------------------------
		//
		//
		//
	}

	/**
	 * Returns the number of observe relations that this resource has to CoAP
	 * clients.
	 * 
	 * @return the observer count
	 * 
	 * 
	 
	 * 没有更改过
	 * 感觉不需要更改
	 */
	/*
	@Override
	public int getObserverCount() {
		return observeRelations.getSize();
	}
	*/



	/**
	 * Notifies all CoAP clients that have established an observe relation with
	 * this resource that the state has changed by reprocessing their original
	 * request that has established the relation.
	 * 
	 * @param filter filter to select set of relations. 
	 *               <code>null</code>, if all clients should be notified.
	 *               
	 
	 * 没有更改过
	 * 感觉不需要更改
	 */
	/*
	@Override
	protected void notifyObserverRelations(final ObserveRelationFilter filter) {
		notificationOrderer.getNextObserveNumber();
		for (ObserveRelation relation : observeRelations) {
			if (null == filter || filter.accept(relation)) {
				relation.notifyObservers();
			}
		}
	}
	*/

	
	//--------------------------------自己添加的方法--------------------------------
	
	//自己添加
	public ConcurrentHashMap<String, InetSocketAddress> getObserveRelations_ip() {
		return observeRelations_ip;
	}
	//自己添加
	public void setObserveRelations_ip(ConcurrentHashMap<String, InetSocketAddress> observeRelations_ip) {
		this.observeRelations_ip = observeRelations_ip;
	}

}
