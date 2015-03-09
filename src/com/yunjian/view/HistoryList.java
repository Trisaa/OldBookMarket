package com.yunjian.view;

public class HistoryList {
	public Node head;
	Node tail;
	int size;
	HistoryList(){
		head = tail = null;
		size = 0;
	}
	
	public HistoryList(String s){
		head = tail = null;
		size = 0;
		if(s != null ){
			String[] str = s.split("¡ü");
			for(int i=0;i<str.length;i++)
			{
//				System.out.println("!!!!!!!!!!!1"+str[i]);
				add(str[str.length-i-1]);
//				size++;
			}
		}
//		System.out.println("finish============"+this.changetoString());
	}
	
	public void add(String s){
//	System.out.println("size =============="+size);
//		if(size == 10){
//			delete();
//		}
//		Node temp = head;
//		if(head==null){
//			head = new Node(s);
//		}
//		else{
//			while(temp.next!=null){
//				temp = temp.next;
//			}
//			temp.next = new Node(s);
//				
//		}
		
//		System.out.println("head================"+head);
		if(head == null){
			head = tail = new Node(s);
//			System.out.println("headdata================"+head.data);
		}else{
			Node temp = new Node(s);
			temp.next = head;
			head = temp; 
		}
		size++;
//		System.out.println("size===================="+size);	
		
		Node temp = head;
//		System.out.println("temp ================= "+temp.next);
		if(head!=null){
//			System.out.println("tempnext====================="+temp.next);
			while(temp.next!=null){
//				System.out.println("temp.next============="+temp.next.data);
//				System.out.println("head============="+head.data);
				if(temp.next.data.equals(head.data)){
					temp.next = temp.next.next;
					if(temp.next==null){
						break;
					}
				}
//				System.out.println("temp.next============="+temp.next.data);
//				System.out.println("temp.next.next============="+temp.next.next);
				
				temp = temp.next;
//				System.out.println("tempnext============"+temp.next);
			}

		}
			
	}
	
	private void delete(){
//		head = head.next;
		
		Node temp = head;
		while(temp.next.next!=null){
			temp = temp.next;
		}
		temp.next = null;
		size--;
	}
	
	public String changetoString(){
		String s = "";
		Node temp = head;
		while(temp!=null){
			s = s + temp.data+"¡ü";
//			System.out.println(temp.data);
			temp = temp.next;
		}
		return s;
	}
	public boolean isEmpty(){
		if(size == 0){
			return true;
		}else{
			return false;
		}
	}

}
	



