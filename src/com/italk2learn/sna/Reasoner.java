package com.italk2learn.sna;

import java.sql.Timestamp;

import MFSeq.FTSequencer;
import MFSeq.WhizzSequencer;

public class Reasoner {
	StudentModel student;
	
	public Reasoner (StudentModel thisStudent){
		student = thisStudent;
	}

	public void getNextTask(StudentNeedsAnalysis sna) {
		int studentChallenge = student.getStudentChallenge();
		String currenExercise = student.getCurrentExercise();
		String nextTask = "";
		
		if (student.getUnstructuredTaskCounter() == 1){
			//sequence next unstructured task
			nextTask = calculateNextUnstructuredTask(currenExercise, studentChallenge);	
		}
		else {
			//switch to structured task
			String currentTask = currenExercise.substring(0, 7);
			nextTask = getNextStructuredTask(currentTask);
			student.setLastExploratoryExercise(currenExercise);
			student.setLastStudentChallenge(studentChallenge);
		}
		sna.setNextTask(nextTask);
	}
	
	private String getNextTaskForUnderChallgenge(String currenExercise){
		String nextTask = "";
		String currentTask = currenExercise.substring(0, 7);
		
		if (currentTask.equals("task2.1")){
			nextTask = "task2.2";
		}
		else if (currentTask.equals("task2.2")){
			String leastUsedRep = getLeastUsedRep();
			nextTask = "task2.4.setA."+leastUsedRep;
		}
		else if (currentTask.equals("task2.4")){
			nextTask = "task2.6.setA";
		}
		else if (currentTask.equals("task2.6")){
			nextTask = "task2.7.setA";
		}
		else if (currentTask.equals("task2.7")){
			String fractionType = currenExercise.substring(8,12);
			if (fractionType.equals("setA")){
				nextTask = "task2.7.setB";
			}
			else {
				nextTask = "task2.7.setC";
			}
		}
		return nextTask;
	}
	
	
	private String getNextTaskForOverChallenged(String currenExercise){
		String nextTask = "";
		String currentTask = currenExercise.substring(0, 7);
		String mostUsedRep = getMostUsedRep();
			
		if (currentTask.equals("task2.1")){
			nextTask = "task2.1";
		}
		else if (currentTask.equals("task2.2")){
			nextTask = "task2.1";
		}
		else if (currentTask.equals("task2.4")){
			nextTask="task2.2";
		}
		else if (currentTask.equals("task2.6")){
			nextTask = getNextTaskWithMostUsedRep("task2.4.setC", mostUsedRep);
		}
		else if (currentTask.equals("task2.7")){
				nextTask = "task2.6.setC";
		}
		return nextTask;
	}
	
	private String getNextTaskWithMostUsedRep(String taskDescription, String mostUsedRep){
		String result = taskDescription+".area";
		
		if (mostUsedRep.equals("liqu")){
			result = ".liqu";
		}
		else if (mostUsedRep.equals("numb")){
			result = ".numb";
		}
		else if (mostUsedRep.equals("sets")){
			result = ".sets";
		}
		return result;
	}
	
	
	private String getMostUsedRep(){
		String result = "area";
		int amountAreaUsed = student.getAmountArea();
		int amountSetUsed = student.getAmountSets();
		int amountNumbUsed = student.getAmountNumb();
		int amountLiguUsed = student.getAmountLiqu();
		
		if ((amountAreaUsed != 0) &&
				(amountAreaUsed >= amountSetUsed) &&
				(amountAreaUsed >= amountNumbUsed) &&
				(amountAreaUsed >= amountLiguUsed)){
			result = "area";
		}
		else if ((amountNumbUsed != 0) &&
				(amountNumbUsed >= amountAreaUsed) &&
				(amountNumbUsed >= amountSetUsed) &&
				(amountNumbUsed >= amountLiguUsed)){
			result = "numb";
		}
		else if ((amountLiguUsed != 0) &&
				(amountLiguUsed >= amountAreaUsed) &&
				(amountLiguUsed >= amountSetUsed) &&
				(amountLiguUsed >= amountNumbUsed)){
			result = "liqu";
		}
		else if ((amountSetUsed != 0) &&
				(amountSetUsed >= amountAreaUsed) &&
				(amountSetUsed >= amountNumbUsed) &&
				(amountSetUsed >= amountLiguUsed)){
			result = "sets";
		}
		return result;
	}
	
	private String getLeastUsedRep(){
		String result = "area";
		int amountAreaUsed = student.getAmountArea();
		int amountSetUsed = student.getAmountSets();
		int amountNumbUsed = student.getAmountNumb();
		int amountLiguUsed = student.getAmountLiqu();
		
		if ((amountAreaUsed == 0) ||
			((amountAreaUsed < amountNumbUsed) &&
			(amountAreaUsed < amountSetUsed) &&
			(amountAreaUsed < amountLiguUsed))){
			result = "area";
		}
		else if ((amountNumbUsed == 0) ||
				((amountNumbUsed < amountAreaUsed) &&
				(amountNumbUsed < amountSetUsed) &&
				(amountNumbUsed < amountLiguUsed))){
			result = "numb";
		}
		else if ((amountLiguUsed == 0) ||
				((amountLiguUsed < amountAreaUsed) &&
				(amountLiguUsed < amountSetUsed) &&
				(amountLiguUsed < amountNumbUsed))){
			result = "liqu";
		}
		else if ((amountSetUsed == 0) ||
				((amountSetUsed < amountAreaUsed) &&
				(amountSetUsed < amountNumbUsed) &&
				(amountSetUsed < amountLiguUsed))){
			result = "sets";
		}
		return result;
	}
	
	private String calculateNextUnstructuredTask(String currenExercise, int studentChallenge){
		String nextTask = "";
		if (studentChallenge == StudentChallenge.overChallenged){
			nextTask = getNextTaskForOverChallenged(currenExercise);
		}
		else if (studentChallenge == StudentChallenge.underChallenged){
			nextTask = getNextTaskForUnderChallgenge(currenExercise); 
		}
		else {
			//this needs to get checked if it is the same for under and appropriately challenged
			nextTask = getNextTaskForUnderChallgenge(currenExercise);
		}
		return nextTask;
	}
	
	private String getNextStructuredTask(String currentTask){
		String nextTask = "";
		boolean inEngland = student.getInEngland();
		
		if (currentTask.equals("task2.1")){
			if (inEngland) nextTask = "MA_GBR_0800CAx0100";
			else nextTask = "Task2_graph_9-12";
			
		}
		else if (currentTask.equals("task2.2")){
			if (inEngland) nextTask = "MA_GBR_1125CAx0100";
			else nextTask = "Task2_graph_9-12";
		}
		else if (currentTask.equals("task2.3")){
			if (inEngland) nextTask = "MA_GBR_0850CAx0100";
			else nextTask = "Task2_graph_9-12";
		}
		else if (currentTask.equals("task2.4")){
			if (inEngland) nextTask = "MA_GBR_0950CAx0100";
			else nextTask = "Task8_graph_1-5";
		}
		else if (currentTask.equals("task2.5")){
			if (inEngland) nextTask = "MA_GBR_1150CAx0300";
			else nextTask = "Task8_graph_1-5";
		}
		else if (currentTask.equals("task2.6")){
			if (inEngland) nextTask = "MA_GBR_1150CAx0100";
			else nextTask = "Task3_graph_1-2";
		}
		else if (currentTask.equals("task2.7")){
			if (inEngland) nextTask = "MA_GBR_1150CAx0100";
			else nextTask = "Task1_graph_3-7";
		}
		
		return nextTask;
		
	}
	
	public void getNextStructuredTask(StudentNeedsAnalysis sna, int whizzStudID, String whizzPrevContID, int prevScore, Timestamp timestamp, String WhizzSuggestion, boolean Trial) {
		String nextTask = "";
		
		if (student.getUnstructuredTaskCounter() == 1){
			//sequence next structured task
			
			if (sna.isWhizzExercise()) {
				nextTask= WhizzSequencer.next(whizzStudID, whizzPrevContID, prevScore, timestamp, WhizzSuggestion, Trial);
			}
			else { 
				nextTask= FTSequencer.next(whizzStudID, whizzPrevContID, prevScore, timestamp, WhizzSuggestion);
			}
			//check if nextTask contains something
		}
		else {
			//switch to next unstructured task
			int studentChallenge = student.getLastStudentChallenge();
			if (studentChallenge == StudentChallenge.flow) studentChallenge = StudentChallenge.underChallenged;
			calculateNextUnstructuredTask(student.getLastExploratoryExercise(), studentChallenge);
		}
		
		sna.setNextTask(nextTask);
	}

}