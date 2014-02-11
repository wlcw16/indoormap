package com.indoormap.framework.common;
//如有问题请到我的博客留言 http://uoloveruo.blog.163.com
import java.util.ArrayList;
import java.util.Collections;
import java.util.Vector;

import android.util.Log;

import com.indoormap.framework.model.Map;
import com.indoormap.framework.model.Point;

public class AStart {
	
	private byte canMoveIndex = 1;	//可以通行的地图图块索引
	
//	private int cellSize = 20;
//	
//	private int mapHeight = 480;
	
	private byte tileSize = 1;	//图块大小
	
	private int rows = 0;			//地图行数
	
	private int cols = 0;			//地图列数
	
	private byte[][] map;		//地图数据
	
	private final byte G_OFFSET = 1;	//每个图块G值的增加值
	
	private int destinationRow;	//目标所在行
	private int destinationCol;	//目标所在列
	
	Vector<Node> closeNode = new Vector<Node>();	//节点关闭列表
	
	Vector<Node> openNode = new Vector<Node>();		//节点开启别表
	

	public AStart(){
		
	}
	
	//设置地图信息属性 
	public void setMap(byte[][] map, int rows, int cols){
		this.map = map;
		this.rows = rows;
		this.cols = cols;
		closeNode.removeAllElements();
		openNode.removeAllElements();
	}
	
	public ArrayList<Point> getPath(int startX, int startY, int destinationX, int destinationY ,Map map){
		long timer = System.currentTimeMillis();
		destinationRow = getRowPosition(destinationY);
		destinationCol = getColPosition(destinationX);
		long timeout = 5000;
		long startTime = System.currentTimeMillis();
		long endTime = System.currentTimeMillis();
		Node startNode = new Node();
		startNode.row = getRowPosition(startY);
		startNode.col = getColPosition(startX);
		startNode.g = 0;
		startNode.h = getH(startNode.row, startNode.col);
		startNode.f = startNode.g + startNode.h;
		openNode.add(startNode);
		Node bestNode;
		while(true){
			bestNode = getBesetNode();
			if(bestNode == null){		//未找到路径
				return null;
			}else if(bestNode.row == getRowPosition(destinationY) 
					&& bestNode.col == getColPosition(destinationX)){
				long time = System.currentTimeMillis() - timer;
				System.out.println("××××××××： " + time);
				ArrayList<Point> result = new ArrayList<Point>();
				Node _Node = bestNode;
				while(_Node.parent != null){
//					System.out.println("x: " + _Node.col + "  y: " + _Node.row);
					result.add(new Point(translateMartrixToMap(_Node.col,false,map),translateMartrixToMap( _Node.row,true,map),map));
					_Node = _Node.parent;
				}
				result.add(new Point(translateMartrixToMap(startX,false,map), translateMartrixToMap(startY,true,map),map));
				Collections.reverse(result);
				return result;
			}
			seachSeccessionNode(bestNode);
			endTime = System.currentTimeMillis();
			Log.i("aStart-BestNode", bestNode.toString());
			if(endTime - startTime > timeout){
//			    return null;
			}
		}
		
	}
	
	/**
	 * 根据传入的节点生成子节点
	 * @param bestNode
	 * @param destinationRow
	 * @param destinationCol
	 */
	private void seachSeccessionNode(Node bestNode){
		int row, col;
		//上部节点
		if(!isOutOfMap(bestNode.row - 1, bestNode.col)){
		if(isCanMove(row = bestNode.row - 1, col = bestNode.col)){
			creatSeccessionNode(bestNode, row, col);
		}
		}
		//下部节点
		if(!isOutOfMap(bestNode.row + 1, bestNode.col)){
		if(isCanMove(row = bestNode.row + 1, col = bestNode.col)){
			creatSeccessionNode(bestNode, row, col);
		}
		}
		//左部节点
		if(!isOutOfMap(bestNode.row, bestNode.col-1)){
		if(isCanMove(row = bestNode.row, col = bestNode.col - 1)){
			creatSeccessionNode(bestNode, row, col);
		}
		}
		//右部节点
		if(!isOutOfMap(bestNode.row, bestNode.col+1)){
		if(isCanMove(row = bestNode.row, col = bestNode.col + 1)){
			creatSeccessionNode(bestNode, row, col);
		}
		}
		closeNode.addElement(bestNode);
		for(int i = 0; i < openNode.size(); i ++){
			if(openNode.elementAt(i).row == bestNode.row 
					&& openNode.elementAt(i).col == bestNode.col){
				openNode.removeElementAt(i);
				break;
			}
		}
	}
	
	private void creatSeccessionNode(Node bestNode, int row, int col){
		Node oldNode = null;
		int g = bestNode.g + G_OFFSET;
		if(!isInClose(row, col)){
			if((oldNode = checkOpen(row, col)) != null){
				if(oldNode.g < g){
					oldNode.parent = bestNode;
					oldNode.g = g;
					oldNode.f = g + oldNode.h;
				}
			}else{
				Node node = new Node();
				node.parent = bestNode;
				node.g = g;
				node.h = getH(row, col);
				node.f = node.g + node.h;
				node.row = row;
				node.col = col;
				openNode.addElement(node);
			}
		}
	}
	
	
	private Node checkOpen(int row, int col){
		Node node = null;
		for(int i = 0; i < openNode.size(); i ++){
			if(openNode.elementAt(i).row == row && openNode.elementAt(i).col == col){
				node = openNode.elementAt(i);
				return node;
			}
		}
		return node;
	}
	
	private boolean isInClose(int row, int col){
		for(int i = 0; i < closeNode.size(); i ++){
			if(closeNode.elementAt(i).row == row && closeNode.elementAt(i).col == col){
				return true;
			}
		}
		return false;
	}
	
	//得到最优节点
	private Node getBesetNode(){
		Node bestNode = null;
		int f = 999999999;
		for(int i = 0; i < openNode.size(); i ++){
			if(openNode.elementAt(i).f < f){
				f = openNode.elementAt(i).f;
				bestNode = openNode.elementAt(i);
			}
		}
		return bestNode;
	}
	
	//得到该图块的H值
	private int getH(int row, int col){
		return (Math.abs(destinationRow - row) + Math.abs(destinationCol - col));
	}
	
	//得到该位置所在地图行
	private int getRowPosition(int y){
		return (y / tileSize);
	}
	
	//得到该位置所在地图列
	private int getColPosition(int x){
		return (x / tileSize);
	}
	
	private boolean isOutOfMap(int col , int row){
	    boolean result = false;
	    if(col < 0 || col >=rows ){
	        result = true;
	    }
	    else if(row < 0 || row >= cols){
	        result = true;
	    }else{
	        result = false;
	    }
	    return result;
	}
	
	//检测该图块是否可通行
	private boolean isCanMove(int col, int row){
	    byte mapItem = map[col][row];
	    boolean result = false;
		if(mapItem == canMoveIndex){
		    result = true;
		}
		else {
		    result = false;
		}
		return result;
	}

    private float translateMartrixToMap(int input , boolean isHeight ,Map map){
	    float result = 0;
//	    if(isHeight){
//	        result = (float)(pp.getMapHeightWithoutZoom()-((input+0.5)*pp.getCellSizeWithOutZoom()));
//	    }else{
	        result =  (float)((input+0.5)*map.getCellSize());
//	    }
	    
        return result;
    }
}
/**
 * 节点类
 * @author 
 *
 */
class Node{
	
	int f;	//该节点路径评分
	
	int g;	//从起始点到该节点的预估距离
	
	int h;	//从该节点到终点的曼哈顿距离（忽略障碍水平垂直移动到终点的距离）
	
	int row;	//该节点所在行
	
	int col;	//该节点所在列
	
	Node parent;	//该节点的父节点
	
	Node[] child = new Node[8];	//该节点的子节点，最多8个
	
	@Override
	public String toString()
	{
	    return "x : " +col+" , y : "+row;
	}
}