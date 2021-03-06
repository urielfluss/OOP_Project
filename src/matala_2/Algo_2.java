package matala_2;
import java.io.FileReader;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Scanner;
import java.util.Set;

import Matala_0.Line_46;
import Matala_0.Readable;
import Matala_0.Wifi4;
import Matala_0.lineData;
import Matala_0.myComperator;

public class Algo_2 {
	String combo_path;
	String arrNoGps_path;
	ArrayList<Line_46> arrNoGps = new ArrayList<Line_46>();
	ArrayList<Line_46> combo = new ArrayList<Line_46>();

	myHash mh = new myHash();

	public Algo_2(String combo_path,String arrNoGps_path ) {
		this.combo_path=combo_path;
		this.arrNoGps_path=arrNoGps_path;
	}

	public void read() {	
		ArrayList<String[]> arr=new ArrayList<String[]>();
		try{
			Scanner scanner=new Scanner(new FileReader(this.combo_path));
			String line;
			while(scanner.hasNextLine()){
				line=scanner.nextLine(); //get the line
				String []results=line.split(",");  //split the line
				if(results[0]!=null)
					arr.add(results);
			}
			scanner.close();
		}catch (Exception e){
			System.out.println("Error: "+ e.getMessage());
		}

		for (int i=0; i<arr.size()-1;i++) {  	//Runs the length of the ArrayList 
			Line_46 line46=new Line_46();
			String temp[]=arr.get(i);
			line46.setLat(Double.parseDouble(temp[2]));
			line46.setLon(Double.parseDouble(temp[3]));
			line46.setAlt(Double.parseDouble(temp[4]));
			line46.setWifiAmount(Integer.parseInt(temp[5]));


			for(int j=7 , k=9; j<temp.length && temp[j]!=null ; j=j+4,k=k+4) {
				Wifi4 wifi=new Wifi4();
				wifi.setMAC(temp[j]);
				wifi.setSignal(temp[k]);
				line46.setListOfWifi(wifi);

			}
			combo.add(line46);
		}

		ArrayList<String[]> arr2=new ArrayList<String[]>();
		try{
			Scanner scanner=new Scanner(new FileReader(this.arrNoGps_path));
			String line;
			while(scanner.hasNextLine()){
				line=scanner.nextLine(); //get the line
				String []results=line.split(",");  //split the line
				if(results[0]!=null)
					arr2.add(results);
			}
			scanner.close();
		}catch (Exception e){
			System.out.println("Error: "+ e.getMessage());
		}

		for (int i=0; i<arr2.size();i++) {  	//Runs the length of the ArrayList 
			Line_46 line46=new Line_46();
			String temp[]=arr2.get(i);
			line46.setTime(temp[0]);
			line46.setId(temp[1]);
			line46.setWifiAmount(Integer.parseInt(temp[5]));
			//			line46.setLat(Double.parseDouble(temp[2]));
			//			line46.setLon(Double.parseDouble(temp[3]));
			//			line46.setAlt(Double.parseDouble(temp[4]));
			for(int  j=7 , k=9 , s=6 , f=8; j<temp.length && temp[j]!=null ; j=j+4,k=k+4,s=s+4,f=f+4) {
				Wifi4 wifi=new Wifi4();
				wifi.setFrequency(temp[f]);
				wifi.setSSID(temp[s]);
				wifi.setMAC(temp[j]);
				wifi.setSignal(temp[k]);
				line46.setListOfWifi(wifi);
			}
			arrNoGps.add(line46);
		}

		for (int i = 0; i < combo.size(); i++) {
			goToHash(combo.get(i));
		}
		System.out.println(mh.mytoString2());

	}	
	private void goToHash(Line_46 line46) {
		ArrayList<Wifi4>arr=new ArrayList<Wifi4>();
		arr=line46.getListOfWifi();
		for (int i = 0; i < arr.size(); i++) {
			mh.add2(arr.get(i).getMAC(), line46);
		}
	}

	public void calculate() {
		int counter=0;	
		for (Line_46 currLine : arrNoGps) {									 //Runs the length of arrNoGps
			Set<Line_46> hs = new HashSet<Line_46>();
			for(int i=0;i<currLine.getWifiAmount();i++) {					//Asking about each mac one by one
				String currNoGpsMac = currLine.getListOfWifi().get(i).getMAC();
				System.out.println("for this mac: "+currNoGpsMac);
				if(mh.hm46.containsKey(currNoGpsMac)) {
					ArrayList<Line_46> hashValue = mh.hm46.get(currNoGpsMac);
					for (int j = 0; j < hashValue.size(); j++) {
						System.out.println(hashValue.get(j).toCsv());
					}
					hs.addAll(hashValue);
				}else {
					System.out.println("i didn't find in the hashmap");
				}
			}
			Set<piAndLine46> setOfPal = new HashSet<piAndLine46>();
			for(Line_46 comline : hs) {
				double pi = 1;
				for(int i=0;i<currLine.getWifiAmount();i++) {
					double noGpsSig = Double.parseDouble(currLine.getListOfWifi().get(i).getSignal());
					int count=0;
					for(int j=0; j<comline.getWifiAmount();j++) {
						double comSig = Double.parseDouble(comline.getListOfWifi().get(j).getSignal());
						if(noGpsSig==comSig) {
							double diff = difference(noGpsSig,comSig);
							double weight = weight(diff,noGpsSig);
							pi = pi*weight;
							count++;
						}
						if(count==0 && j==comline.getWifiAmount()-1) {
							double diff = 100;
							double weight = weight(diff,noGpsSig);
							pi = pi*weight;

						}	
					}
				}
				piAndLine46 pal = new piAndLine46();
				pal.setLine46(comline);
				pal.setPi(pi);
				setOfPal.add(pal);
			}

			ArrayList<piAndLine46> arrOfPal = new ArrayList<piAndLine46>();
			arrOfPal.addAll(setOfPal);
			Collections.sort(arrOfPal, new myComperator());
//			arrOfPal.sort(Comparator.comparing(piAndLine46::getPi));
			
			if(arrOfPal.size()==1) {
				piAndLine46 pal1 = arrOfPal.get(0);
				double wlat1 = pal1.getLine46().getLat()*pal1.getPi();
				double wlon1 = pal1.getLine46().getLon()*pal1.getPi();
				double walt1 = pal1.getLine46().getAlt()*pal1.getPi();
				double latSum=wlat1, lonSum=wlon1, altSum=walt1;
				double piSum = pal1.getPi();
				double ansLat = latSum/piSum;
				double ansLon = lonSum/piSum;
				double ansAlt = altSum/piSum;
				this.arrNoGps.get(counter).setAlt(ansAlt);
				this.arrNoGps.get(counter).setLon(ansLon);
				this.arrNoGps.get(counter).setLat(ansLat);
				//Added missing coordinate go to next line

				counter++;
			}else if(arrOfPal.size()==2) {
				piAndLine46 pal1 = arrOfPal.get(0);
				piAndLine46 pal2 = arrOfPal.get(1);
				double wlat1 = pal1.getLine46().getLat()*pal1.getPi();
				double wlon1 = pal1.getLine46().getLon()*pal1.getPi();
				double walt1 = pal1.getLine46().getAlt()*pal1.getPi();
				double wlat2 = pal2.getLine46().getLat()*pal2.getPi();
				double wlon2 = pal2.getLine46().getLon()*pal2.getPi();
				double walt2 = pal2.getLine46().getAlt()*pal2.getPi();
				double latSum=wlat1+wlat2, lonSum=wlon1+wlon2, altSum=walt1+walt2;
				double piSum = pal1.getPi()+pal2.getPi();
				double ansLat = latSum/piSum;
				double ansLon = lonSum/piSum;
				double ansAlt = altSum/piSum;
				this.arrNoGps.get(counter).setAlt(ansAlt);
				this.arrNoGps.get(counter).setLon(ansLon);
				this.arrNoGps.get(counter).setLat(ansLat);
				//Added missing coordinate go to next line

				counter++;
			}else if(arrOfPal.size()>2) {
				piAndLine46 pal1 = arrOfPal.get(0);
				piAndLine46 pal2 = arrOfPal.get(1);
				piAndLine46 pal3 = arrOfPal.get(2);

				double wlat1 = pal1.getLine46().getLat()*pal1.getPi();
				double wlon1 = pal1.getLine46().getLon()*pal1.getPi();
				double walt1 = pal1.getLine46().getAlt()*pal1.getPi();
				double wlat2 = pal2.getLine46().getLat()*pal2.getPi();
				double wlon2 = pal2.getLine46().getLon()*pal2.getPi();
				double walt2 = pal2.getLine46().getAlt()*pal2.getPi();
				double wlat3 = pal3.getLine46().getLat()*pal3.getPi();
				double wlon3 = pal3.getLine46().getLon()*pal3.getPi();
				double walt3 = pal3.getLine46().getAlt()*pal3.getPi();
				double latSum=wlat1+wlat2+wlat3 , lonSum=wlon1+wlon2+wlon3, altSum=walt1+walt2+walt3;
				double piSum = pal1.getPi()+pal2.getPi()+pal3.getPi();

				double ansLat = latSum/piSum;
				double ansLon = lonSum/piSum;
				double ansAlt = altSum/piSum;
				this.arrNoGps.get(counter).setAlt(ansAlt);
				this.arrNoGps.get(counter).setLon(ansLon);
				this.arrNoGps.get(counter).setLat(ansLat);
				//Added missing coordinate go to next line

				counter++;

			}else {
				System.out.println("no way");
				counter++;
			}
						
			System.out.println("\nThis is arr of pal:\n");
			System.out.println("size: "+arrOfPal.size());
			for (int j = 0; j < arrOfPal.size(); j++) {
				System.out.println(arrOfPal.get(j).myToString());

			}
			System.out.println("\n******I finished line "+counter+" ********\n");
		}
		
		System.out.println("arr with gps: ");
		for (int i = 0; i < arrNoGps.size(); i++) {
			System.out.println(arrNoGps.get(i).toCsv());
		}
		
	}
	
	private double weight(double diff, double noGpsSig) {
		double weight = 10000/( Math.pow(diff, 0.4)*Math.pow(noGpsSig, 2)  );
		return weight;
	}
	private double difference(double noGpsSig, double comSig) {
		double diff = Math.abs(noGpsSig-comSig);
		if(diff == 0)
			diff = 3;
		return diff;
	}

	public void write(String name) {
		try {
			FileWriter outfile;
			outfile = new java.io.FileWriter(name, true);
			for(Line_46 curr : this.arrNoGps) {
				outfile.write( curr.toCsv() );
			}
			outfile.close();
		} catch (IOException e) {
			System.out.println("Error in write() method!");
			e.printStackTrace();
		} 
	}

	public String mytoString() {
		String sline="This is combo:\n";
		for (int j = 0; j < combo.size(); j++) {
			sline=sline+combo.get(j).toCsv();
		}
		sline=sline+"**************************\n\n\nThis is arrNoGps:\n";
		for (int j = 0; j < arrNoGps.size(); j++) {
			sline=sline+arrNoGps.get(j).toCsv();
		}
		return sline+"\n";
	}
}
