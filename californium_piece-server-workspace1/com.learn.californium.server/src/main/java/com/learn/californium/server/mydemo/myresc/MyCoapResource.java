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
 * ����Ҫ ������һ����ͬ�ı��� ��ȡ �������������server������ client
 * ��Ϊ �������ȸ���ԭ���Ĵ���
 * ���� ������ ��ǰ ���� list_varA
 * ����������һ�� mylist_varA
 * 
 * ����һ���ٷ��ķ��� 
 * add_varA_element(Object e){
 * 	list_varA.add(e) 
 * }
 * 
 * ��ô �Ҿ� �� �����ͬ�ķ�����
 * add_varA_element(Object e){
 * 	list_varA.add(e) 
 * 	mylist_varA.add(e) 	//���
 * } 
 * 
 * �����ķ�ʽ �� mylist_varA ����ɾ�Ĳ������һ��
 * ʹ�� list_varA ��  mylist_varA ����һ��
 * 
 * ���ڶ� list_varA ��ɾ�Ĳ� �������Ǻܼ򵥵Ĵ���, �����㲻�ù� �ܶ�������ҵ���߼�
 * ֻ��Ҫ���� ��������ɾ�Ĳ� �� �� mylist_varA ����ͬ�Ĳ��輴��
 * 
 * ���ַ�ʽ, ����Ҫ�Ķ�ԭ���ı���, ֻ���������˶���һ�� ���� ȥͬ������
 *  
 *  
 *  �ҽ�������۵ĳ��� ������Ϊ
 *  ��Ҫ����������resource�� ���е�client �� ip
 *  
 *  �� observeRelations ���� 
 *  	�� observers  observeRelations
 *  ���� ���������� ���� ������������, ��Щ������ һЩ������ �ᱻ����, 
 *  	���Ժ���ר��������������  my_observers ���� my_observeRelations ȥͬ������IP�������������
 *  
 *  ���Զ��ھ�����˵, �Ҿͱ��� Ip�б�ͺ�, ��Ϊclient��ip�����ױ�����
 *  �����������
 *  
 * add_varA_element(Object e){
 * 	list_varA.add(e) 
 * 	mylist_varA.add(e) 	//���
 * } 
 * 
 * Ȼ��ĳ�
 * add_varA_element(Object e){
 * 	list_varA.add(e) 
 * 	mylist_varA_ip.add(e.getIp()) 	//���, ������Ϊ�� e.getIp()
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
	/* �Լ��� The list of observers (not CoAP observer). */
	private List<String> observers_ip;

	/* �Լ��� The the list of CoAP observe relations. */
	//private List<String> observeRelations_ip;
	/** The set of observe relations */
	// �ο��� ObserveRelationContainer
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
	 
	 
	 * û�и��Ĺ�
	 * �о�����Ҫ����
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
	 * û�и��Ĺ�
	 * �о�����Ҫ����, ��Ϊ����Ҫɾ��resource�� ���relation,������;������� removeObserveRelation
	 * 
	 * ����ɾ��Ip���� removeObserveRelation ������������������
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
	 * û�и��Ĺ�
	 * �о�����Ҫ����, ��Ϊ����Ҫɾ��resource�� ���relation,������;������� removeObserveRelation
	 * 
	 * ����ɾ��Ip���� removeObserveRelation ������������������
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
	 * �ȴ�����
	 */
	@Override
	public synchronized void addObserver(ResourceObserver observer) {
		super.addObserver(observer);
		//observers.add(observer);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.californium.core.server.resources.Resource#removeObserver(org.eclipse.californium.core.server.resources.ResourceObserver)
	 
	 * 
	 * �ȴ�����
	 */
	@Override
	public synchronized void removeObserver(ResourceObserver observer) {
		super.removeObserver(observer);
		//observers.remove(observer);
	}









	/* (non-Javadoc)
	 * @see org.eclipse.californium.core.server.resources.Resource#setPath(java.lang.String)
	 *
	 * û�и��Ĺ�
	 * �о�����Ҫ����
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
	 * û�и��Ĺ�
	 * �о�����Ҫ����
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
	 * ���Ĺ� ����, ��������ʱ���ø���
	 */
	//@Override
	//private void adjustChildrenPath() {
		//String childpath = path + name + /* since 23.7.2013 */ "/";
		// ����
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
		//�Լ�д��
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
		//�Լ�д��
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
	 
	 * û�и��Ĺ�
	 * �о�����Ҫ����
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
	 
	 * û�и��Ĺ�
	 * �о�����Ҫ����
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

	
	//--------------------------------�Լ���ӵķ���--------------------------------
	
	//�Լ����
	public ConcurrentHashMap<String, InetSocketAddress> getObserveRelations_ip() {
		return observeRelations_ip;
	}
	//�Լ����
	public void setObserveRelations_ip(ConcurrentHashMap<String, InetSocketAddress> observeRelations_ip) {
		this.observeRelations_ip = observeRelations_ip;
	}

}
