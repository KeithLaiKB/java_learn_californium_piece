package com.learn.californium.server.compare.scenario1;

import org.eclipse.californium.core.CoapServer;

import com.learn.californium.server.compare.Com_MyObserverResource_Con_Mwe;



/**
 * 
 * 
 * <p>
 * 							description:																</br>	
 * &emsp;						MWE means minimal working example										</br>
 * &emsp;						MWE ��˼����  �򻯵�����														</br>
 * &emsp;						for testing the observer												</br>
 * 																										</br>
 * 
 * </p>
 *
 *
 * @author laipl
 *
 */
public class TestObserverMain_Mwe  {

	
	public static void main(String[] args) {
		// TODO Auto-generated method stub

		CoapServer server = new CoapServer(5656);
		//
		Com_MyObserverResource_Con_Mwe myobResc1 = new Com_MyObserverResource_Con_Mwe("hello_observer");
		//------------------------operate server-------------------------------------
		//
		server.add(myobResc1);
		//
		server.start(); // does all the magic
		//
		//
		// ͣ��һ��ʱ�� ��server��������
		try {
			Thread.sleep(30000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//
		//
		//
		// ��Ϊ���ǵ�resource���� timer,
		// �������� destroy ��server�Ժ� , resource���������е�
		// in my opinion, we should apply a standard process
		// so we need to stop the resource
		myobResc1.stopMyResource();
		//
		//
		// ����Main���� ����һ��ʱ��, ���ǿ��Է���resourceû�������, Ҳ����ζ�� ȷʵ������
		// ��ʵ �����Ŀ��Բ���, ֻ�������ж�resource�Ƿ������,
		// ���resource û�ص�, �Ϳ��� �����ʱ���� ������resource�����
		try {
			Thread.sleep(10000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// destroy server
		// because the resource use the timer
		server.destroy();
		System.out.println("destroy the server and stop the resource timer finished!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
	}

	
	


}
