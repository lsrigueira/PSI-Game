import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;

import java.util.Random;

public class FixedAgent extends Agent {

    private AID mainAgent;
    private int myId, opponentId;
    private int N, S, R, I, P;
    private ACLMessage msg;

    protected void setup() {
        //Register in the yellow pages as a player
        DFAgentDescription dfd = new DFAgentDescription();
        dfd.setName(getAID());
        ServiceDescription sd = new ServiceDescription();
        sd.setType("Player");
        sd.setName("Game");
        dfd.addServices(sd);
        
        try {
            DFService.register(this, dfd);
        } catch (FIPAException fe) {
            fe.printStackTrace();
        }
        addBehaviour(new Play());
        System.out.println("RandomAgent " + getAID().getName() + " is ready.");

    }

    protected void takeDown() {
        //Deregister from the yellow pages
        try {
            DFService.deregister(this);
        } catch (FIPAException e) {
            e.printStackTrace();
        }
        System.out.println("RandomPlayer " + getAID().getName() + " terminating.");
    }

    private class Play extends CyclicBehaviour {
        Random random = new Random(1000);
        AID mainAgent;
        public RandomAgent agent;
        int id;
        int nplayers;
        int endowment;
        int roundavg;
        float pd;
        int numgames;
        int fixed;

        public void setFixed(int X){
            this.fixed = X;
        }

        public int getFixed(){
            return this.fixed;
        }

        /*public Play(RandomAgent X){
            this.agent = X;
        }*/

        @Override
        public void action() {
            while(true){
                try{
                    ACLMessage mensaxe = receive();
                    if(mensaxe!=null){
                        this.mainAgent=mensaxe.getSender();
                        ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
                        msg.addReceiver(this.mainAgent);
                        switch(mensaxe.getContent().substring(0,3).toLowerCase()){
                            case "id#":
                                this.setFixed((int)Math.round((Math.random()*4)));
                                String aux[] = mensaxe.getContent().split("#");
                                this.id = Integer.parseInt(aux[1]);
                                String auxcomas[] = aux[2].split(",");
                                this.nplayers = Integer.parseInt(auxcomas[0]);
                                this.endowment = Integer.parseInt(auxcomas[1]);
                                this.roundavg = Integer.parseInt(auxcomas[2]);
                                this.pd = Float.parseFloat(auxcomas[3]);
                                this.numgames = Integer.parseInt(auxcomas[4]);
                                break;
                            case "new":    
                               /* this.setFixed((int)Math.round((Math.random()*4)));
                                msg.setOntology("Verbose");
                                msg.setContent(getAID().getLocalName()+":\tEmpezamos un novo xogo");
                                send(msg);
                                msg.setOntology("Other thing");
                                msg.setContent("startup received");
                                send(msg);*/    
                                break;
                            case "act":
                                msg.setOntology("HiAll");
                                msg.setContent("Action#"+this.getFixed());
                                send(msg);
                                break;
                            case "res":
                                //System.out.println("Chegaron os resultados");
                                break;
                            case "gam":
                                System.out.println("Rematou o xogo");
                                break;
                            default:
                                System.out.println("Esta chegando algo que non entendemos--->"+mensaxe.getContent());
                                break;
                        }
                    }else{
                        block();
                    }
                
               }catch(Exception e){
                   e.printStackTrace();
               }
            }
        }
            
        /**
         * Validates and extracts the parameters from the setup message
         *
         * @param msg ACLMessage to process
         * @return true on success, false on failure
         */
        private boolean validateSetupMessage(ACLMessage msg) throws NumberFormatException {
            int tN, tS, tR, tI, tP, tMyId;
            String msgContent = msg.getContent();

            String[] contentSplit = msgContent.split("#");
            if (contentSplit.length != 3) return false;
            if (!contentSplit[0].equals("Id")) return false;
            tMyId = Integer.parseInt(contentSplit[1]);

            String[] parametersSplit = contentSplit[2].split(",");
            if (parametersSplit.length != 5) return false;
            tN = Integer.parseInt(parametersSplit[0]);
            tS = Integer.parseInt(parametersSplit[1]);
            tR = Integer.parseInt(parametersSplit[2]);
            tI = Integer.parseInt(parametersSplit[3]);
            tP = Integer.parseInt(parametersSplit[4]);

            //At this point everything should be fine, updating class variables
            mainAgent = msg.getSender();
            N = tN;
            S = tS;
            R = tR;
            I = tI;
            P = tP;
            myId = tMyId;
            return true;
        }

        /**
         * Processes the contents of the New Game message
         * @param msgContent Content of the message
         * @return true if the message is valid
         */
        public boolean validateNewGame(String msgContent) {
            int msgId0, msgId1;
            String[] contentSplit = msgContent.split("#");
            if (contentSplit.length != 2) return false;
            if (!contentSplit[0].equals("NewGame")) return false;
            String[] idSplit = contentSplit[1].split(",");
            if (idSplit.length != 2) return false;
            msgId0 = Integer.parseInt(idSplit[0]);
            msgId1 = Integer.parseInt(idSplit[1]);
            if (myId == msgId0) {
                opponentId = msgId1;
                return true;
            } else if (myId == msgId1) {
                opponentId = msgId0;
                return true;
            }
            return false;
        }
    }
}