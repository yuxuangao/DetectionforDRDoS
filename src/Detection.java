
import java.io.File;

import weka.classifiers.Classifier;
import weka.classifiers.lazy.IB1;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.ConverterUtils.DataSource;

public class Detection {

	private Classifier mclass = new IB1();
	private boolean isAttacked = false;
	private String trainfileAddress;
	
	public Detection(String trainfileAddress) throws Exception{
		setTrainFileAddress(trainfileAddress);
	}
	
	public boolean isAttacked(){
		return isAttacked;
	}
	
	private void trainClassifier() throws Exception{
		DataSource trainsource = new DataSource(trainfileAddress);
		Instances traindata = trainsource.getDataSet();
		traindata.setClassIndex(traindata.numAttributes()-1);
		mclass.buildClassifier(traindata);
	}
	
	public void setClassifier(Classifier mclass) throws Exception{
		this.mclass = mclass;
		trainClassifier();
		
	}
	
	public void setTrainFileAddress(String address) throws Exception{
		trainfileAddress = address;
		trainClassifier();
	}
	
	public void detect(int ipnumber,int udpsize,int udpnumber,int dudpnumber,int portnumber) 
			throws Exception{
		Instance testdata = new Instance(5);
		testdata.setValue(0, ipnumber);
		testdata.setValue(1, udpsize);
		testdata.setValue(2, udpnumber);
		testdata.setValue(3, dudpnumber);
		testdata.setValue(4, portnumber);
		isAttacked = mclass.classifyInstance(testdata)==0.0;
	}
	
	public String getTrainFileAddress(){
		return trainfileAddress;
	}
	
	public String getTrainFileName(){
		File file =new File(trainfileAddress);
		return file.getName();
	}
	
	public Object[][] getTrainingData() throws Exception{
		DataSource trainsource = new DataSource(trainfileAddress);
		Instances traindata = trainsource.getDataSet();
		traindata.setClassIndex(traindata.numAttributes()-1);
		Object[][] result = new Object[traindata.numInstances()][traindata.numAttributes()];
		for (int i=0;i<traindata.numInstances();i++){
			for (int j=0;j<traindata.numAttributes()-1;j++){
				result[i][j] = traindata.instance(i).value(j);
			}
			result[i][traindata.numAttributes()-1] = traindata.instance(i).classValue()==0?"yes":"no";
		}
		return result;
	}
	
	public int getnumTrainingInstances() throws Exception{
		DataSource trainsource = new DataSource(trainfileAddress);
		Instances traindata = trainsource.getDataSet();
		traindata.setClassIndex(traindata.numAttributes()-1);
		return traindata.numInstances();
	}
	
	public int getnumTrainingClasses() throws Exception{
		DataSource trainsource = new DataSource(trainfileAddress);
		Instances traindata = trainsource.getDataSet();
		traindata.setClassIndex(traindata.numAttributes()-1);
		return traindata.numAttributes();
	}
	
	public String[] getClassesName() throws Exception{
		DataSource trainsource = new DataSource(trainfileAddress);
		Instances traindata = trainsource.getDataSet();
		traindata.setClassIndex(traindata.numAttributes()-1);
		String[] classesname = new String[traindata.numAttributes()];
		for (int i=0;i<classesname.length;i++){
			classesname[i] = traindata.attribute(i).name();
		}
		return classesname;
	}
	
}
