package homework;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Random;

public class HW1 {
	List<List<String>> res;
	char[][] board;
	char[][] temp_board;
	Random rnd = new Random();
	public static void main(String[] args) {
		HW1 hw1 = new HW1();
		
		hw1.parseInput();
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
            
            while ((str = br.readLine()) != null) {
            		sb.append(str + "\n");
            		str = str.trim();
            		if(rowNum == 0)
            			firstLine = str;
            		if(rowNum == 1)
            			edge = Integer.parseInt(str);
            		//System.out.println(firstLine);
            		if(rowNum == 2)
            			lizard = Integer.parseInt(str);
            		if(rowNum == 3) {
            			board = new char[str.length()][str.length()];
            		}
            		if(i - 3 >= 0 ) {
            			for(int j = 0; j < str.trim().length(); j++) {
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
            if(firstLine.equals("DFS")) {
            		//DFS sfs = new DFS();
            		DFS(edge, board, lizard);
            }
            if(firstLine.equals("BFS")) {
            		BFS(edge, board, lizard);
            }
            if(firstLine.equals("SA")) {
            		SA(edge, board, lizard);
            }
         // write string to file  
            FileOutputStream fos = new FileOutputStream(output_path, true);  
            //BufferedWriter bw = new BufferedWriter(fos);  

     
            if(res.size() == 0) {
        			System.out.println("FAIL");
        			fos.write(("FAIL").getBytes());
        			fos.write("\r\n".getBytes());
            } else {
            	System.out.println("the size of list is : " + res.size());
            		fos.write(("OK").getBytes());
            		fos.write("\r\n".getBytes());// 写入一个换行  
            		for(int k = 0; k < res.get(0).size(); k++) {
            			fos.write((res.get(0).get(k)).getBytes());
                		fos.write("\r\n".getBytes());// 写入一个换行 
            		}
            }
        		System.out.println(res.size());
            fos.close();  
            //w.close();
         
		} catch(FileNotFoundException e) {  
            e.printStackTrace();  
        } catch (IOException e) {  
        	    e.printStackTrace();  
        }  
	}
	//--------------------------------------SA--------------------------------------------
	public List<List<String>> SA(int n, char[][] board, int lizard) {
		res = new ArrayList<>();
		int[][] intBoard = new int[n][n];
		for(int i = 0; i < n; i++) {
			for(int j = 0; j < n; j++) {
				intBoard[i][j] = board[i][j] - '0';
			}
		}
		sa(intBoard, n, lizard);
		return res;
	}
	
	private void sa(int[][] intBoard, int n, int lizard) {
		int tempLizard = lizard;
		while(tempLizard > 0) {
			int x = rnd.nextInt(n);
			int y = rnd.nextInt(n);
			if(intBoard[x][y] != 2 && intBoard[x][y] != 1) {
				intBoard[x][y] = 1;
				tempLizard--;
			}
		}
		
		
		//initiate cost
		int cost = 0;
		for(int i = 0; i < n; i++ ) {
			for(int j = 0; j < n; j++) {
				if(intBoard[i][j] == 1) {
					int count = getCost(intBoard, i, j, n);
					cost += count;
				} 
			}
		}
		int countNum = 0;
		double temperature;
		double inittemp = 1000000000;
		long preTime = System.currentTimeMillis();
		for(temperature = inittemp; temperature > 0 && (cost != 0) ; temperature--) {
			if(System.currentTimeMillis() - preTime > 280000)
				break;
			System.out.println(System.currentTimeMillis());
			countNum++;
			System.out.println("cost is : " + cost + "the set is: " + countNum);
			int[][] nextState = new int[n][n];
			for(int i = 0; i < n; i++) {
				for(int j = 0; j < n; j++) {
					nextState[i][j] = intBoard[i][j];
				}
			}
			int lizardPos = rnd.nextInt(lizard);
			int x = 0, y = 0;
			double delta;
		    double probability;
	   	    double rand;
			for(int i = 0; i < n; i++) {
				for(int j = 0; j < n; j++) {
					if(intBoard[i][j] == 1) lizardPos--;
					if(lizardPos < 0) {
						x = i;
						y = j;
						break;
					}
				}
				if(lizardPos < 0) break;
			}
			int newX = rnd.nextInt(n), newY = rnd.nextInt(n);
			while(nextState[newX][newY] == 1 || nextState[newX][newY] == 2) {
				newX = rnd.nextInt(n);
				newY = rnd.nextInt(n);
			}
			nextState[newX][newY] = 1;
			nextState[x][y] = 0;
			int currCost = cost + 2 * getCost(nextState, newX, newY, n) - 2 * getCost(intBoard, x, y, n);
			delta = cost - currCost;
			probability = Math.exp(delta / temperature);
			probability = Math.exp((double)(delta) / (0.1 + (2 * (temperature + 1) / (double)inittemp)));
			rand = Math.random();
			if(delta > 0) {
				intBoard = nextState;
				cost = currCost; 
			} else if(rand <= probability) {
				intBoard = nextState;
				cost = currCost; 
			}
		}
		
		if(cost == 0) {
			char[][] charBoard = new char[n][n];
			for(int i = 0; i < n; i++) {
				for(int j = 0; j < n; j++) {
					charBoard[i][j] = (char) (intBoard[i][j] + '0');
				}
			}
			res.add(construct(charBoard));
			return;
		}
		
	}
	
	private int getCost(int[][] intBoard, int x, int y, int n) {
		int conflict = 0;
		int[][] table = {{1, 0}, {-1, 0}, {0, 1}, {0, -1}, {-1, -1}, {1, 1}, {1, -1}, {-1, 1}};
		for(int[] tab : table) {
			int moveX = x + tab[0];
			int moveY = y + tab[1];
			while(moveX >= 0 && moveX < n && moveY >= 0 && moveY < n) {
				if(intBoard[moveX][moveY] == 2) break;
				if(intBoard[moveX][moveY] == 1) conflict++;
				moveX += tab[0];
				moveY += tab[1];
			}
		}
		return conflict;
	}
	
	
	/*
	 int i = x, j = y;
		while(i >= 0) {
			if(board[i][y] == '0')
				i--;
			else if(board[i][y] == '1')
				return false;
			else if(board[i][y] == '2')
				break;
		}
		i = x;
		//same row
		while(j >= 0) {
			if(board[x][j] == '0')
				j--;
			else if(board[x][j] == '1')
				return false;
			else if(board[x][j] == '2')
				break;
		}
		j = y;
		//135
		while(i >= 0 && j >= 0) {
			if(board[i][j] == '0') {
				i--; j--;
			}
			else if(board[i][j] == '1') {
				return false;
			}
			else if(board[i][j] == '2') {
				break;
			}	
		}
		i = x; j = y;
		//45
		while(i < board.length && j >= 0) {
			if(board[i][j] == '0') {
				i++; j--;
			}
			else if(board[i][j] == '1') {
				return false;
			}
			else if(board[i][j] == '2') {
				break;
			}
		}
		return true;
	*/
	
	
	
	//--------------------------------------BFS--------------------------------------------
	public List<List<String>> BFS(int n, char[][] board, int lizard) {
		res = new ArrayList<>();
		bfs(board, 0, res, lizard);
		return res;
	}
	private void bfs(char[][] board, int colIndex, List<List<String>> res, int lizard) {
		HashMap<Integer, int[]> lastLizard = new HashMap<>();//for last Index
		HashMap<Integer, char[][]> record = new HashMap<>();//for recording char[][]
		Queue<Integer> keyQueue = new LinkedList<>();
		keyQueue.offer(0);
		lastLizard.put(0, new int[]{0, 0});
		record.put(0, board);
		while(!keyQueue.isEmpty()) {
			int char_index = keyQueue.poll();
			char[][] curr = record.get(char_index);
			int[] position = lastLizard.get(char_index);
			int x = position[0], y = position[1];
			while(y < curr.length) {
				while(x < curr.length) {
					if(curr[x][y] != '2' && curr[x][y] != '1') {
						//if(validate(curr, x, y)) {
						if(validateBFS(curr, x, y, curr.length)) {
							char[][] temp = new char[board.length][board.length];
							int lizardNum = 0;
							for(int j = 0; j < curr.length; j++) {
								for(int k = 0; k < curr[0].length; k++) {
									if(curr[j][k] == '1')
										lizardNum++;
									temp[j][k] = curr[j][k];
								}
							}
							//System.out.println("lizard num is" + lizardNum);
							temp[x][y] = '1';
							if(lizardNum + 1 == lizard) {
								res.add(construct(temp));
								return;
							}
							
							//if(x + 1 == curr.length)
							lastLizard.put(keyQueue.size() + 1, new int[] {x, y});
							record.put(keyQueue.size() + 1, temp);
							keyQueue.offer(keyQueue.size() + 1);
						}
					}
					
						x += 1;
				}
				x = 0;
				y += 1;
			}
		}
		
	}
	private boolean validateBFS(char[][] charBoard, int x, int y, int n) {
		int[][] tempBoard = new int[n][n];
		for(int i = 0; i < n; i++) {
			for(int j = 0; j < n; j++) {
				tempBoard[i][j] = charBoard[i][j] - '0';
			}
		}
		
		int[][] table = {{1, 0}, {-1, 0}, {0, 1}, {0, -1}, {-1, -1}, {1, 1}, {1, -1}, {-1, 1}};
		for(int[] tab : table) {
			int moveX = x + tab[0];
			int moveY = y + tab[1];
			while(moveX >= 0 && moveX < n && moveY >= 0 && moveY < n) {
				if(tempBoard[moveX][moveY] == 2) break;
				if(tempBoard[moveX][moveY] == 1) return false;
				moveX += tab[0];
				moveY += tab[1];
			}
		}
		return true;
	}
	//--------------------------------------DFS--------------------------------------------
	public List<List<String>> DFS(int n, char[][] board, int lizard) {
        res = new ArrayList<>();
        System.out.println(lizard);
       
        dfs(board, 0, 0, res, lizard);
        //
        
        return res;
    }
	
	private void dfs(char[][] board, int rowIndex, int colIndex, List<List<String>> res, int lizard) {
		if(lizard == 0) {
			res.add(construct(board));
			return;
		}
		
        while(rowIndex < board.length && colIndex < board.length && lizard > 0) {
	        	if(rowIndex == board.length - 1 && colIndex < board.length - 1 && board[rowIndex][colIndex] == '0') {
	        		if(!validate(board, rowIndex, colIndex)) {
	        			rowIndex = 0;
		        		colIndex++;
	        		}
	        	} else if(rowIndex == board.length - 1 && colIndex < board.length - 1 && board[rowIndex][colIndex] != '0') {
	        		rowIndex = 0;
	        		colIndex++;
	        	}
	    		if(board[rowIndex][colIndex] != '2') {
	    			if(validate(board, rowIndex, colIndex)) {
	                board[rowIndex][colIndex] = '1';
	                if(rowIndex < board.length - 1) {
	                		dfs(board, rowIndex + 1, colIndex, res, lizard - 1);
	                } else {
	                		dfs(board, 0, colIndex + 1, res, lizard - 1);
	                }
	                //lizard--;
	                
	                board[rowIndex][colIndex] = '0';
	                //lizard++;
	            }
	    		}
	    		rowIndex++;
	    		
        }
		
		
    }

	private boolean validate(char[][] board, int x, int y) {
		int i = x, j = y;
		while(i >= 0) {
			if(board[i][y] == '0')
				i--;
			else if(board[i][y] == '1')
				return false;
			else if(board[i][y] == '2')
				break;
		}
		i = x;
		//same row
		while(j >= 0) {
			if(board[x][j] == '0')
				j--;
			else if(board[x][j] == '1')
				return false;
			else if(board[x][j] == '2')
				break;
		}
		j = y;
		//135
		while(i >= 0 && j >= 0) {
			if(board[i][j] == '0') {
				i--; j--;
			}
			else if(board[i][j] == '1') {
				return false;
			}
			else if(board[i][j] == '2') {
				break;
			}	
		}
		i = x; j = y;
		//45
		while(i < board.length && j >= 0) {
			if(board[i][j] == '0') {
				i++; j--;
			}
			else if(board[i][j] == '1') {
				return false;
			}
			else if(board[i][j] == '2') {
				break;
			}
		}
		return true;
			
	}

    private List<String> construct(char[][] board) {
        List<String> list = new ArrayList<>();
        for(int i = 0; i < board.length; i++) {
            String s = new String(board[i]);
            list.add(s);
        }
        //System.out.println(list);
        return list;
    }
}

/*
class DFS {
	public DFS() {
		// TODO Auto-generated constructor stub
	}
}
*/
/*
private void dfs(char[][] board, int colIndex, List<List<String>> res, int lizard) {
    //if(colIndex == board.length || lizard == 0) {
	
		//if(colIndex == board.length) {
        
		//temp_board = board;
		//return;
        //return board;
    //}
	if(colIndex == board.length) {
		res.add(construct(board));
		return;
	}
	if(lizard == 0 ) {
		res.add(construct(board));
	}
    
	for(int i = 0; i < board.length && lizard > 0; i++) {
    	//判断是否为2
		//System.out.println(i);
		if(board[i][colIndex] == '1')
				continue;	
    		if(board[i][colIndex] != '2') {
    			if(validate(board, i, colIndex)) {
                board[i][colIndex] = '1';
                if(i < board.length) {
                		dfs(board, colIndex, res, lizard - 1);
                } else {
                		dfs(board, colIndex + 1, res, lizard - 1);
                }
                //lizard--;
                
                board[i][colIndex] = '0';
                lizard++;
            }
    		}
    }
	
}
*/

/*
private boolean validate1(char[][] board, int x, int y) {
    for(int i = 0; i < board.length; i++) {
        for(int j = 0; j <= y; j++) {
        		if(board[i][j] == '1') {
        			if(x + j == y + i) {//在135度对角线上
        				if(i < x && j < y) {
        					boolean flag = false;
        					int m = i, n = j;
        					while (m < x && n < y) {
        						if(board[m][n] == '2')
        							flag = true;
        						if(board[m][n] == '1')
        							flag = false;
        						m++; n++;
        					}
        					return flag;
        				}
        				
        			}
        			else if(i + j == x + y) {//在45度对角线上
        				if(i == x + 1 && j == y - 1)
        					return false;
        				else {
        					boolean flag = false;
        					int m = i - 1, n = j + 1;
        					while(m > x && n < y) {
        						if(board[m][n] == '2')
        							flag = true;
        						if(board[m][n] == '1')
        							flag = false;
        						m--; n++;
        					}
        					return flag;
        				}
        			}
        			else if(i == x) {//在同一行
        				if(j == y - 1)
        					return false;
        				else {
        					boolean flag = false;
        					int n = j + 1;
        					while(n < y) {
        						if(board[i][n] == '2')
        							flag = true;
        						if(board[i][n] == '1')
        							flag = false;
        						n++;
        					}
        					return flag;
        				}
        			}
        			else if(j == y) {//在同一列
        				boolean flag = false;
        				int m = i;
        				while(m < x) {//只需要检查到倒数第二行
        					if(board[m][j] == '2')
            					flag = true;
        					if(board[m][j] == '1')
        						flag = false;
        					m++;
        				}
        				return flag;
        			}
        		}
        }
    }
    return true;
}
*/