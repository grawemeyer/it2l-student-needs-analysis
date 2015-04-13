package com.italk2learn.sna;

import java.util.ArrayList;
import java.util.List;

import ptdFromAmplitudes.CreateWav;
import ptdFromAmplitudes.PtdFromAmplitudes;

public class Analysis {
	StudentModel student;
	
	public Analysis(StudentModel thisStudent){
		student = thisStudent;
	}

	
	public void analyseSound(byte[] audioByteArray){
		String wavname;
        List<byte[]> exampleChunks = new ArrayList<byte[]>();
        exampleChunks.add(audioByteArray);

        int numberOfChunksToCombine = exampleChunks.size();

        CreateWav wavcreation = new CreateWav();
        for (int i = 0; i < numberOfChunksToCombine; i++) {
            wavcreation.addChunk(exampleChunks.get(i));
        }

        // Initialize ptd classifier:
        PtdFromAmplitudes ptdAmpl = new PtdFromAmplitudes();

        // get perceived task difficulty (ptd):

        // Create wav from the last x (here numberOfChunksToCombine) chunks (x = seconds/5)
        wavname = wavcreation.createWavFileMonoOrStereo(numberOfChunksToCombine);
        
        int result = ptdAmpl.getPTD(wavname);
        
        
        if (result == -1){
        	System.out.println("PTD: no result");
        }
        else if (result == 1){
        	System.out.println("PTD: overchallenged");
        }
        else if (result == 2){
        	System.out.println("PTD: flow");
        }
        else if (result == 3){
        	System.out.println("PTD: underchallenged");
        }
        		
        if (student == null) student = new StudentModel();
		student.setPTD(result);
	}
	
	public void getNextStructuredTask(StudentNeedsAnalysis sna){
		calculateStudentChallenge();
		Reasoner reasoner = new Reasoner(student);
		reasoner.getNextStructuredTask(sna);
	}


	public void analyseFeedbackAndSetNewTask(StudentNeedsAnalysis sna) {
		calculateStudentChallenge();
		Reasoner reasoner = new Reasoner(student);
		reasoner.getNextTask(sna);
	}
	
	private void calculateStudentChallenge(){
		int studentChallenge = StudentChallenge.flow;
		int valuePTP = student.getPTD();
		int feedbackAmount = calculateFeedbackAmount();
		int mostAffectiveState = calculateAffectiveState();
		int lotsFeedback = 7;
		int someFeedback = 4;
		
		System.out.println(":: calculateStudentChallenge ::");
		System.out.println(":: feedbackAmount :: "+feedbackAmount);
		System.out.println("lotsFeedback: "+lotsFeedback);
		System.out.println("someFeedback: "+someFeedback);
		/*
		 * perceived task difficulty classifier PTD
		 * 1=overchallenged
		 * 2=flow
		 * 3=underchallenged
		 */
		if (valuePTP == 1){
			if (!student.getIncludesAffect()){
				studentChallenge = StudentChallenge.overChallenged;
			}
			else {
				if (feedbackAmount > lotsFeedback){
					studentChallenge = StudentChallenge.overChallenged;
				}
				else if (feedbackAmount > someFeedback){
					if (mostAffectiveState == Affect.flow){
						studentChallenge = StudentChallenge.flow;
					}
					else {
						studentChallenge = StudentChallenge.overChallenged;
					}
				}
				else {
					if ((mostAffectiveState == Affect.boredom) ||
						(mostAffectiveState == Affect.flow)){
						studentChallenge = StudentChallenge.flow;
					}
					else {
						studentChallenge = StudentChallenge.overChallenged;
					}
				}
			}
		}
		else if (valuePTP == 2){
			if (feedbackAmount > lotsFeedback){
				if (student.getIncludesAffect() && (mostAffectiveState == Affect.flow)){
					studentChallenge = StudentChallenge.flow;
				}
				else {
					studentChallenge = StudentChallenge.overChallenged;
				}
			}
			else if (feedbackAmount > someFeedback){
				if (!student.getIncludesAffect()){
					studentChallenge = StudentChallenge.flow;
				}
				else {
					if ((mostAffectiveState == Affect.boredom) ||
						(mostAffectiveState == Affect.flow)){
						studentChallenge = StudentChallenge.flow;
					}
					else {
						studentChallenge = StudentChallenge.overChallenged;
					}
				}
			}
			else {
				if (!student.getIncludesAffect()) {
					studentChallenge = StudentChallenge.overChallenged;
				}
				else {
					if ((mostAffectiveState == Affect.boredom) ||
						(mostAffectiveState == Affect.flow)){
						studentChallenge = StudentChallenge.flow;
					}
					else {
						studentChallenge = StudentChallenge.overChallenged;
					}
				}
			}
		}
		else if (valuePTP == 3){
			if (feedbackAmount > lotsFeedback){
				if (!student.getIncludesAffect()){
					studentChallenge = StudentChallenge.overChallenged;
				}
				else {
					if ((mostAffectiveState == Affect.confusion) ||
						(mostAffectiveState == Affect.frustration)){
						studentChallenge = StudentChallenge.overChallenged;
					}
					else if (mostAffectiveState == Affect.boredom){
						studentChallenge = StudentChallenge.underChallenged;
					}
					else {
						studentChallenge = StudentChallenge.flow;
					}
				}
			}
			else if (feedbackAmount > someFeedback){
				if (!student.getIncludesAffect()){
					studentChallenge = StudentChallenge.underChallenged;
				}
				else {
					if (mostAffectiveState == Affect.confusion){
						studentChallenge = StudentChallenge.overChallenged;
					}
					else if (mostAffectiveState == Affect.frustration){
						studentChallenge = StudentChallenge.flow;
					}
					else {
						studentChallenge = StudentChallenge.underChallenged;
					}
				}
			}
			else {
				if (!student.getIncludesAffect()){
					studentChallenge = StudentChallenge.underChallenged;
				}
				else {
					if (mostAffectiveState == Affect.confusion){
						studentChallenge = StudentChallenge.overChallenged;
					}
					else if (mostAffectiveState == Affect.frustration){
						studentChallenge = StudentChallenge.flow;
					}
					else {
						studentChallenge = StudentChallenge.underChallenged;
					}
				}
			}
			
		}
		else if (valuePTP == -1){
			if (feedbackAmount > lotsFeedback){
				studentChallenge = StudentChallenge.overChallenged;
			}
			else if (feedbackAmount > someFeedback){
				studentChallenge = StudentChallenge.flow;
			}
			else {
				studentChallenge = StudentChallenge.underChallenged;
			}
		}
		printStudentChallenge(studentChallenge);
		student.setStudentChallenge(studentChallenge);
	}

	private void printStudentChallenge(int studentChallenge){
		if (studentChallenge == StudentChallenge.overChallenged){
			System.out.println("::: student is over challenged :::");
		}
		else if (studentChallenge == StudentChallenge.flow){
			System.out.println("::: student is in flow :::");
		}
		else if (studentChallenge == StudentChallenge.underChallenged){
			System.out.println("::: student is under challenged :::");
		}
	}

	private int calculateAffectiveState() {
		int amountConfusion = student.getAmountConfusion();
		int amountFrustration = student.getAmountFrustration();
		int amountBoredom = student.getAmountBoredom();
		int amountFlow = student.getAmountFlow();
		int result = Affect.flow;
		
		if ((amountConfusion > amountFrustration) &&
				(amountConfusion > amountBoredom) &&
				(amountConfusion > amountFlow)){
			result = Affect.confusion;
		}
		else if ((amountFrustration > amountConfusion) &&
				(amountFrustration > amountBoredom) &&
				(amountFrustration > amountFlow)){
			result = Affect.frustration;
		}
		else if ((amountBoredom > amountConfusion) &&
				(amountBoredom > amountFrustration) &&
				(amountBoredom > amountFlow)){
			result = Affect.boredom;
		}
		
		else if ((amountFlow > amountConfusion) &&
				(amountFlow > amountFrustration) &&
				(amountFlow > amountBoredom)){
			result = Affect.flow;
		}
		
		return result;
	}


	private int calculateFeedbackAmount() {
		int result = 0;
		
		result = student.getAmountNextStep() + student.getAmountProblemSolving() + 
				student.getAmountReflection() + student.getAmountAffectBoosts();
		
		return result;
	}
	
}
