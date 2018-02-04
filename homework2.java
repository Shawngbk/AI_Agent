
package homework;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
//时间剩下很少很少，该怎么确定层数
public class homework {
	List<List<String>> res;
	char[][] board;
	char[][] temp_board;
	Random rnd = new Random();
	int[]index = new int[2];
	int availableTime = 0;
	long startTime;
	boolean isBreak;
	long beginTime;
	float time;
	public static void main(String[] args) {
		homework hw2 = new homework();
		hw2.parseInput();
	}

	public void parseInput() {
		String path = "src/homework/input_test.txt";
		String output_path = "src/homework/output.txt";
		try {
			// read file content from file 
			StringBuffer sb = new StringBuffer(path);
			FileReader reader = new FileReader(path);  
            BufferedReader br = new BufferedReader(reader);  
  
            String str = null;  
            int rowNum = 0;
            int i = 0;
            board = null;
            String firstLine = "";
            int edge = 0;
            int lizard = 0;
            int fruitType = 0;
            
            
            startTime = System.currentTimeMillis();
            
            
            while ((str = br.readLine()) != null) {
            		sb.append(str + "\n");
            		str = str.trim();
            		if(rowNum == 0)
            			edge = Integer.parseInt(str);
            		//System.out.println(edge);
            		if(rowNum == 1)
            			fruitType = Integer.parseInt(str);
            		//System.out.println(fruitType);
            		if(rowNum == 2) {
            			time = Float.parseFloat(str);
            			availableTime = (int)(time * 990) / (edge * edge);
            		}
            		
            		System.out.println(availableTime);
            		if(rowNum == 3) {
            			board = new char[str.length()][str.length()];
            		}
            		if(i - 3 >= 0 ) {
            			for(int j = 0; j < str.trim().length(); j++) {
            				if(str.trim().charAt(j) == '*')
            					board[i - 3][j] = '*';
            				else
            					board[i - 3][j] = str.trim().charAt(j);
            				//System.out.println(board[i - 3][j]);
                		}
            		}
            		rowNum++;
            		i++;
            		//System.out.println("flag" + str); 
            }
            br.close();
            reader.close();
           
            makeDecision(board, edge); 
    			System.out.println(String.valueOf((char)(index[1] + 65)) + (index[0] + 1));
    		
         // write string to file  
            FileOutputStream fos = new FileOutputStream(output_path, true);  
            
            //	System.out.println("the size of list is : " + res.size());
            	fos.write((String.valueOf((char)(index[1] + 65)) + (index[0] + 1)).getBytes());
            	fos.write("\r\n".getBytes());// 写入一个换行  
            	for(int k = 0; k < res.get(0).size(); k++) {
            		fos.write((res.get(0).get(k)).getBytes());
                	fos.write("\r\n".getBytes());// 写入一个换行 
            	}
            fos.close(); 
            long endTime = System.currentTimeMillis();
            System.out.println("Total time is : " + (endTime - startTime) + "ms");
         
		} catch(FileNotFoundException e) {  
            e.printStackTrace();  
        } catch (IOException e) {  
        	    e.printStackTrace();  
        }  
	}
	private void makeDecision(char[][] board, int edge) {
		boolean[][] visited = new boolean[edge][edge];
		int score = 0;
		int maxVal = Integer.MIN_VALUE;;
		int alpha = Integer.MIN_VALUE;
		int beta = Integer.MAX_VALUE;
		isBreak = false;
		int level = 0;
		if(((board.length * board.length)) > time * 1.5)
			level = 2;
		else if(board.length > 10)
		    level = 3;
		else if(board.length <= 10)
			level = 3;
		beginTime = System.currentTimeMillis();
		for(int i = 0; i < edge; i++) {
			for(int j = 0; j < edge; j++) {
				
				if(visited[i][j] || board[i][j] == '*')
					continue;
				else {
					char[][] state = new char[edge][edge];
					for(int m = 0; m < edge; m++) {
						for(int n = 0; n < edge; n++) {
							state[m][n] = board[m][n];
						}
					}
					
					score = findConnection(state, visited, edge, i, j, board[i][j], score);
					getCurrState(state);
					System.out.println("level is :  " + level);
					
					int currVal = minVal(state, alpha, beta, level, score * score, beginTime);
					
					if(isBreak) {
						level--;
						isBreak = false;
						beginTime = System.currentTimeMillis();
					}
					if(currVal > maxVal) {
						index[0] = i;
						index[1] = j;
						maxVal = currVal;
					}
				}
				alpha = Math.max(alpha, maxVal);
			}
		}
		char[][] fstate = new char[edge][edge];
		for(int m = 0; m < edge; m++) {
			for(int n = 0; n < edge; n++) {
				fstate[m][n] = board[m][n];
			}
		}
		res = new ArrayList<>(); 
		score = findConnection(fstate, visited, edge, index[0], index[1], board[index[0]][index[1]], score);
		//res.add(construct(fstate));
		//System.out.println(res);
		getCurrState(fstate);
		
		res.add(construct(fstate));
		System.out.println("i is : " + index[0] + "j is : " + index[1]);
		System.out.println(res);
	}
	private int maxVal(char[][] tempState, int alpha, int beta, int depth, int currScore, long beginTime) {
		int runTime = 0;
		if(time > 190)
			runTime = 15;
		else if(time > 100 && time < 190)
			runTime = 10;
		else if(((board.length * board.length)) > time * 1.5)
			runTime = 6;
		//System.out.println(currScore);
		if(System.currentTimeMillis() - beginTime > availableTime * runTime) {
			isBreak = true;
			return currScore;
		}
		if(depth == 0)
			return currScore;
		boolean[][] visited = new boolean[tempState.length][tempState.length];
		int maxVal = Integer.MIN_VALUE;
		int score = 0;
		for(int i = 0; i < tempState.length; i++) {
			for(int j = 0; j < tempState.length; j++) {
				
				if(visited[i][j] || board[i][j] == '*')
					continue;
				else {
					char[][] state = new char[tempState.length][tempState.length];
					for(int m = 0; m < tempState.length; m++) {
						for(int n = 0; n < tempState.length; n++) {
							state[m][n] = tempState[m][n];
						}
					}
					score = findConnection(state, visited, tempState.length, i, j, board[i][j], 0);
					getCurrState(state);
					maxVal = Math.max(maxVal, minVal(state, alpha, beta, depth - 1, currScore + score * score, beginTime));
					
					if(maxVal >= beta)
						return maxVal;
					alpha = Math.max(alpha, maxVal);
				}
				
			}
		}
		return maxVal;
	}
	
	private int minVal(char[][] tempState, int alpha, int beta, int depth, int currScore, long beginTime) {
		int runTime = 0;
		if(time > 190)
			runTime = 15;
		else if(time > 100 && time < 190)
			runTime = 10;
		else if(((board.length * board.length)) > time * 1.5)
			runTime = 6;
		if(System.currentTimeMillis() - beginTime > availableTime * runTime) {
			isBreak = true;
			return currScore;
		}
		if(depth == 0)
			return currScore;
		boolean[][] visited = new boolean[tempState.length][tempState.length];
		int minVal = Integer.MAX_VALUE;
		int score = 0;
		for(int i = 0; i < tempState.length; i++) {
			for(int j = 0; j < tempState.length; j++) {
				
				if(visited[i][j] || board[i][j] == '*')
					continue;
				else {
					char[][] state = new char[tempState.length][tempState.length];
					for(int m = 0; m < tempState.length; m++) {
						for(int n = 0; n < tempState.length; n++) {
							state[m][n] = tempState[m][n];
						}
					}
					score = findConnection(state, visited, tempState.length, i, j, board[i][j], 0);
					getCurrState(state);
					minVal = Math.min(minVal, maxVal(state, alpha, beta, depth - 1, currScore - score * score, beginTime));
					
					if(minVal <= alpha)
						return minVal;
					beta = Math.min(beta, minVal);
				}
				
			}
		}
		return minVal;
	}

	
	private int findConnection(char[][] state, boolean[][] visited, int edge, int i, int j, char mark, int score) {
		if(i < 0 || j < 0 || i == edge || j == edge || state[i][j] != mark)
			return 0;
		
		//if(state[i][j] == mark && visited[i][j])
		//	return;
		visited[i][j] = true;
		state[i][j] = '*';
		score = 1 + 
		findConnection(state, visited, edge, i + 1, j, mark, score) +
		findConnection(state, visited, edge, i - 1, j, mark, score) +
		findConnection(state, visited, edge, i, j + 1, mark, score) +
		findConnection(state, visited, edge, i, j - 1, mark, score);
		return score;
	}
	
	private void getCurrState(char[][] state) {
		for(int col = 0; col < state.length; col++) {
			int row = state.length - 1;
			int count = 0;
			
			//boolean isStar = false;
			
			while(row >= 0) {
				if(state[row][col] == '*') 
					count++;
				else {
					if(count > 0) {
						state[row + count][col] = state[row][col];
						state[row][col] = '*';
						//count--;
					}
				}
				row--;
			}
		}
	}
	
	private List<String> construct(char[][] cboard) {
        List<String> list = new ArrayList<>();
        for(int i = 0; i < cboard.length; i++) {
            String s = new String(cboard[i]);
            list.add(s);
        }
        //System.out.println(list);
        return list;
    }
}
