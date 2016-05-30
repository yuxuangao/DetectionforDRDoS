
import java.awt.BasicStroke;
import java.awt.Color;
import java.util.Vector;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

public class DFDChart {

	private int xmax = 100;
	
	private Vector<Integer> data = new Vector<Integer>();
	
	private XYSeries chartdata = new XYSeries("");
	private XYSeriesCollection chartdatacoll = new XYSeriesCollection();
	private JFreeChart chart = ChartFactory.createXYLineChart("", "", "", chartdatacoll,
			PlotOrientation.VERTICAL,false,false,false);
	private ChartPanel chartpanel = new ChartPanel(chart);
	/**
	 * 新建一个显示图
	 */
	public DFDChart(){
		addData(0);
		chartdatacoll.addSeries(chartdata);
		setchartFormat();
	}
	
	private void setchartFormat(){
		chart.setBackgroundPaint(null);
		XYPlot xyplot = chart.getXYPlot();
		xyplot.getDomainAxis().setVisible(false);
		xyplot.getRangeAxis().setVisible(false);
		xyplot.setDomainGridlinesVisible(false);
		xyplot.setBackgroundPaint(Color.black);
		
		XYItemRenderer linerenderer = xyplot.getRenderer();
		BasicStroke line = new BasicStroke(3.0f);
		linerenderer.setSeriesPaint(0, Color.green);
		linerenderer.setSeriesStroke(0, line);
	}
	
	private void dataRefresh(){
		dataClear();
		for (int i=0;i<data.size();i++){
			chartdata.add(i, data.get(i));
		}
		chart.getXYPlot().getDomainAxis().setRange(data.size()-xmax, data.size()-1);
	}
	
	private void dataClear(){
		for (int i=chartdata.getItemCount()-1;i>=0;i--){
			chartdata.remove(i);
		}
	}
	/**
	 * 清空显示数据
	 */
	public void clear(){
		data.removeAllElements();
	}
	/**
	 * 添加一个数据
	 * @param value 添加的数据值
	 */
	public void addData(int value){
		if (data.size()<xmax){
			data.addElement(value);
		}
		else{
			for (int i=data.size()-xmax;i<xmax-1;i++){
				data.set(i+xmax-data.size(), data.get(i+1));
			}
			data.set(xmax-1, value);
			data.setSize(xmax);
		}
		dataRefresh();
	}
	
	public int getXmax(){
		return xmax;
	}
	
	public void setXmax(int xmax){
		this.xmax = xmax;
		this.dataRefresh();
	}
	
	public ChartPanel getChartPanel(){
		return chartpanel;
	}
}
