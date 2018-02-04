package homework;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class calibrate {
	Random rnd = new Random();
	char[][] board;
	int[] index = new int[2];
	List<List<String>> res;
	public static void main(String[] args) {
		calibrate test = new calibrate();
		test.CheckBox();
	}
	private void BuildBox() {
		for(int i = 4; i < 27; i++)
			CheckBox(i);
	}
	private void CheckBox(int len) {
		boolean flag = false;
		board = new char[len][len];
		for(int i = 0; i < len; i++) {
			for(int j = 0; j < len; j++) {
				board[i][j] = (char)(rnd.nextInt(10) + '0');
			}
		}
		for(int i = 0; i < 10; i++) {
			long startTime = System.currentTimeMillis();
			long endTime = System.currentTimeMillis();
			while(endTime - startTime > 30000 && !flag) {
				flag = makeDecision(board, len, i);
				endTime = System.currentTimeMillis();
			}	
		}
	}
	private void CheckBox() {
		board = new char[20][20];
		
		for(int i = 0; i < 20; i++) {
			for(int j = 0; j < 20; j++) {
				board[i][j] = (char)(rnd.nextInt(10) + '0');
			}
		}
		//System.out.println(board[10][10]);
		long startTime = System.currentTimeMillis();
		flag = makeDecision(board, 20);
		long endTime = System.currentTimeMillis();
		System.out.println("Total time is : " + (endTime - startTime) + "ms");
		String output_path = "src/homework/calibration.txt";
		try {
			FileOutputStream fos = new FileOutputStream(output_path, true);  
            
            //	System.out.println("the size of list is : " + res.size());
            	fos.write(String.valueOf(((endTime - startTime) / (double)(20 * 20 * 20 * 20))).getBytes());
            	fos.write("\r\n".getBytes());// 写入一个换行  
//            	for(int k = 0; k < res.get(0).size(); k++) {
//            		fos.write((res.get(0).get(k)).getBytes());
//                	fos.write("\r\n".getBytes());// 写入一个换行 
//            	}
            fos.close(); 
            
            
		} catch(FileNotFoundException e) {  
            e.printStackTrace();  
        } catch (IOException e) {  
        	    e.printStackTrace();  
        }
		
	}
	

	private boolean makeDecision(char[][] board, int edge, int d) {
		boolean[][] visited = new boolean[edge][edge];
		int score = 0;
		int maxVal = Integer.MIN_VALUE;;
		int alpha = Integer.MIN_VALUE;
		int beta = Integer.MAX_VALUE;
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
					//score++;
					
					//System.out.println("the score it : " +  score);
					getCurrState(state);
					int currVal = minVal(state, alpha, beta, d, score);
					if(currVal > maxVal) {
						index[0] = i;
						index[1] = j;
						maxVal = currVal;
					}
//					if(maxVal >= beta) {
//						index[0] = i;
//						index[1] = j;
//						char[][] fstate = new char[edge][edge];
//						for(int m = 0; m < edge; m++) {
//							for(int n = 0; n < edge; n++) {
//								fstate[m][n] = board[m][n];
//							}
//						}
//						score = findConnection(fstate, visited, edge, i, j, board[i][j], score);
//						getCurrState(fstate);
//						res = new ArrayList<>();
//						res.add(construct(fstate));
//						System.out.println(res);
//					}
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
		return true;
	}
	private int maxVal(char[][] tempState, int alpha, int beta, int depth, int currScore) {
		//System.out.println(currScore);
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
					maxVal = Math.max(maxVal, minVal(state, alpha, beta, depth - 1, currScore + score));
					
					if(maxVal >= beta)
						return maxVal;
					alpha = Math.max(alpha, maxVal);
				}
				
			}
		}
		return maxVal;
	}
	
	private int minVal(char[][] tempState, int alpha, int beta, int depth, int currScore) {
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
					minVal = Math.min(minVal, maxVal(state, alpha, beta, depth - 1, currScore - score));
					
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
