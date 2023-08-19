/*
Developer: Hamidreza Navaei
All right reserved.
*/

import java.util.*;

public class Main {
    public static void main(String... args) {
        Scanner stdSc = new Scanner(System.in);

        System.out.printf("Convert Right-Regular Grammar to Finite Automata%n" +
                "-------------------------------------------------%n" +
                "Notes: - Enter the rules of your grammar in format of this example: A -> aA | bb | *%n" +
                "       - Use \"*\" for showing empty string(epsilon).%n" +
                "       - Enter \"end\" after entering all rules of your grammar.%n" +
                "       - For any problem with this app contact the developer in social medias: Hamidreza Navaei +989155966755%n");
        System.out.printf("Enter your right-regular grammar:%n%n");

        List<Terminal> terminalList = new ArrayList<>();

        List<String> finalStates = new ArrayList<>();

        while(!(stdSc.hasNext("End") || stdSc.hasNext("end")|| stdSc.hasNext("END"))){
            Scanner lineScanner = new Scanner(stdSc.nextLine());
            Terminal newTerminal = new Terminal(lineScanner.next()); lineScanner.next();
            while(true){
                String expression = lineScanner.next();
                char lastChar=expression.charAt(expression.length()-1);
                if(Character.isUpperCase(lastChar)) {
                    newTerminal.addTransition(Character.toString(lastChar), expression.substring(0, expression.length() - 1));
                }
                else if(Character.isLowerCase(lastChar)) {
                    newTerminal.addTransition("D0", expression);
                    if(!finalStates.contains(String.format("D%d", expression.length()-1)))
                        finalStates.add(String.format("D%d", expression.length()-1));
                }
                else if(lastChar=='*'){
                    newTerminal.setEpsilonTransition();
                    if(newTerminal.getTerminalName().equals("S"))
                        finalStates.add("S");
                }
                else{
                    System.out.println("Input Error");
                    return;
                }
                if(lineScanner.hasNext())
                    lineScanner.next();
                else break;
            }
            terminalList.add(newTerminal);
        }

        for (Terminal e: terminalList) {
            //int lTransitionList=e.transitionList.size();
            for (int i=0;i<e.transitionList.size();i++) {
                Transition e2 = e.getTransitionList().get(i);
                if(e2.getDestinationState().length()!=1)
                    continue;
                int iDestinationState=searchTerminals(terminalList,e2.getDestinationState());
                if(terminalList.get(iDestinationState).hasEpsilonTransition()) {
                    e.addTransition("D0", e2.getReadingString());
                    if (!finalStates.contains(String.format("D%d", e2.getReadingString().length() - 1)))
                        finalStates.add(String.format("D%d", e2.getReadingString().length() - 1));

                    if (terminalList.get(iDestinationState).getTransitionList().size() == 0){
                        e.getTransitionList().remove(i);
                        i--;
                    }
                }
//                if(terminalList.get(iDestinationState).hasEpsilonTransition() &&
//                        terminalList.get(iDestinationState).getTransitionList().size()==0){
//                    e.getTransitionList().remove(i); i--;
//                }
            }
        }

        System.out.printf("%nEquivalent automata:%n");

        for (Terminal e: terminalList) {
            e.printTransitions();
        }

        System.out.printf("%nNotes: - each line shows a transition.%n" +
                "       - The letter in parenthesis shows the reading string for transition.%n"+
                "       - Final states are: ");
        for (int i=0;i<finalStates.size();i++) {
            System.out.print(finalStates.get(i));
            if(i!=finalStates.size()-1) System.out.printf(", ");
        }
        System.out.println();
    }

    public static int searchTerminals(List<Terminal> terminals, String terminalName){
        for (int i = 0; i < terminals.size(); i++) {
            if(terminals.get(i).getTerminalName().equals(terminalName)) return i;
        }
        return -1;
    }
}

class Terminal{
    String terminalName;
    boolean hasEpsilonTransition;
    List<Transition> transitionList = new ArrayList<>();

    public Terminal(String terminalName){
        this.terminalName=terminalName;
    }

    public void addTransition(String destinationState,String readingString) {
        Transition newTransition = new Transition(terminalName, destinationState, readingString);
        if(!transitionList.contains(newTransition))
            transitionList.add(newTransition);
    }

    public List<Transition> getTransitionList() {
        return transitionList;
    }

    public String getTerminalName() {
        return terminalName;
    }

    public boolean hasEpsilonTransition() {
        return hasEpsilonTransition;
    }

    public void setEpsilonTransition() {
        this.hasEpsilonTransition = true;
    }

    public void printTransitions(){
        for (Transition e:transitionList) {
            System.out.println(e);
        }
    }
}

class Transition{
    String beginningState,destinationState,readingString;
    public Transition(String beginningState,String destinationState,String readingString){
        this.beginningState=beginningState;
        this.destinationState=destinationState;
        setReadingString(readingString);
    }

    public void setReadingString(String readingString){
        this.readingString=readingString;
    }

    public String getReadingString() {
        return readingString;
    }

    public String getDestinationState() {
        return destinationState;
    }

    @Override
    public boolean equals(Object obj) {
        Transition otherTransition = (Transition) obj;
        return (this.beginningState.equals(otherTransition.beginningState)) &&
                (this.destinationState.equals(otherTransition.destinationState)) &&
                (this.readingString.equals(otherTransition.readingString));
    }

    @Override
    public String toString() {
        String beginningState,destinationState=this.beginningState;
        StringBuilder allTransitions = new StringBuilder();
        for(int i=0;i<readingString.length();i++){
            beginningState=destinationState;
            destinationState=(!this.destinationState.equals("D0") && i==readingString.length()-1 ? this.destinationState:String.format("D%d",i));
            allTransitions.append(String.format("%s -> %s (%c)",beginningState,destinationState,readingString.charAt(i)));
            if(i<readingString.length()-1)allTransitions.append(String.format("%n"));
        }
        return allTransitions.toString();
    }
}