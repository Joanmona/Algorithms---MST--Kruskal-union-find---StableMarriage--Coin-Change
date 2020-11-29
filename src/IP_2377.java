import integrationproject.algorithms.Algorithms;
import integrationproject.model.Ant;
import integrationproject.model.BlackAnt;
import integrationproject.model.Edge;
import integrationproject.model.RedAnt;
import integrationproject.utils.CheckResults;
import integrationproject.utils.InputHandler;
import integrationproject.utils.Visualize;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;


/**
 *
 * @author <Μοσχίδου Ιωάννα>
 * @aem <2377>
 * @email <mosperioa@csd.auth.gr>
 */
public class IP_2377 extends Algorithms {

    public static void main(String[] args) {
        checkParameters(args);
        
        //create Lists of Red and Black Ants
        int flag = Integer.parseInt(args[1]);
        ArrayList<RedAnt> redAnts = new ArrayList<>();
        ArrayList<BlackAnt> blackAnts = new ArrayList<>();
        if (flag == 0) {
            InputHandler.createRandomInput(args[0], Integer.parseInt(args[2]));
        }
        InputHandler.readInput(args[0], redAnts, blackAnts);

        
        IP_2377 algs = new IP_2377();
        
        //debugging options
        boolean visualizeMST = false;
        boolean visualizeSM = false;
        boolean printCC = false;
        boolean evaluateResults = true;

        if(visualizeMST){
            int[][] mst = algs.findMST(redAnts, blackAnts);
            if (mst != null) {
                Visualize sd = new Visualize(redAnts, blackAnts, mst, null, "Minimum Spanning Tree");
                sd.drawInitialPoints();
            }
        }

        if(visualizeSM){
            int[][] matchings = algs.findStableMarriage(redAnts, blackAnts);
            if (matchings != null) {
                Visualize sd = new Visualize(redAnts, blackAnts, null, matchings, "Stable Marriage");
                sd.drawInitialPoints();
            }
        }

        if(printCC){
            int[] coinChange = algs.coinChange(redAnts.get(0), blackAnts.get(0)); 
            System.out.println("Capacity: " + redAnts.get(0).getCapacity());
            for(int i = 0; i < blackAnts.get(0).getObjects().length; i++){
                System.out.println(blackAnts.get(0).getObjects()[i] + ": " + coinChange[i]);
            }
        }
        
        if(evaluateResults){
            System.out.println("\nEvaluation Results");
            algs.evaluateAll(redAnts, blackAnts);
        }
    }

    
    
    
    
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /**
     * In this method I am going to materialize the MST. 
     * I have the ArrayLists with red and black ants.
     * I am taking the size of one of the Lists to know the number the number of the ants for each color.
     * I am creating a 2D array (ArrayLeaders) in witch i will keep the information about each ant and its leader.
     * It has numberOfAllAnts rows and eight columns. The first four columns the color,ID,X,Y of each ant, and the other four have
     * the color,ID,X,Y of the ant's Leader. 
     * First of all , I am not creating sets but i am finding the leaders that each ant would have if it was in an actual set.
     * At the beginning, each ant is on its own. So basically each ant is its own Leader. So in the array ArrayLeaders i am filling 
     * the first four columns with the information of each ant, and at the rest four for their leaders, we are adding the same information
     * as the first four for each ant.
     * The information about the leaders will change each time the leader of the ant changes. 
     * When we are going to check if an edge will be added to the final tree or not we are going to be need to check
     * if the two ants of this edge belongs in the same set(they have the same leader).
     * So based on the color and ID we are finding the two ants in the ArrayLeaders and we compare the (x,y) of their Leaders.
     * If it's the same then we don't add this edge i the final tree, if not, then we are changing the leader of the finishing ant
     * along with all the other ants that belong in the same set as it (they have the same leader as the finishing ant before the change).
     * But for me to do that i need to create the edges first. So i made a new Class name NewEdge. There i create objects
     * witch contains the information(color,ID,x,y) about both ants that they create an edge along with
     * the weight(distance) of the edge. The edges that i created are edges between two red ants, two black ants and red and black
     * ants. Basically everyone with everyone.I also create an ArrayList(AllEdges) in witch I saved all those objects.
     * But i didn't add the edges between black-red cause I had already added the edges between red-black ants.
     * Now that we have and the ArrayList with all the possible edges ,I a sorting this ArrayList with the help of a comparator
     * so the objects in this ArrayList will be sorted based of their saved weight(distance) from the one with the smallest weight
     * to the one with the one with the biggest weight.
     * Then I am taking each object in this array and checking if it will be in the final tree, by comparing the leaders of the
     * two ants. If their leaders have the same x and y they are in the same set, so this edge isn't needed..
     * But if they have different y they aren't in the same set so we need this edge. Also if they
     * have different x they aren't in the same set so we need the edge.
     * If the edge is going to be used I am adding it in a new ArrayList name EdgeWeNeed.
     * At the end with the EdgeWeNeed I am creating the array that is wanted to be returned.
     * 
     * 
     * 
     * @param redAnts
     * @param blackAnts
     * @return 
     */
    @Override
    public int[][] findMST(ArrayList<RedAnt> redAnts, ArrayList<BlackAnt> blackAnts) {
        
        int Size1 = redAnts.size(); /*The size of the ArrayList redAnts*/
        //System.out.println("Size1="+Size1);
        int Size2 = 2*Size1;/*The nuber of rows of a 2D array in witch I will keep the ID,X,Y, and color of the ants and their leaders.*/
                                                
       // System.out.println("Size2="+Size2);
        
        
    
        /*Now I will create 2D array Size2 X 8 in witch I will save the information
        that i need for each ant and its leader.
        At the beginning, each ant is on its own, so its leader is going to be it self.
         The Array has 8 columns. The first four are for the ant and the rest for its leader.
         The first column has the color, the next the ID, the next the X and the last the Y.
         The same order goes for the next four colums*/
        
        
        double ArrayLeaders [][] = new double [Size2][8];
        int i=0;
        int j=0;
        
        /*i will start with filling the Array*/
        /*i will start with the RedAnts*/
        for(RedAnt red : redAnts)
        {
            //Information about the ant
            ArrayLeaders[i][j]=0; //we are adding red ants
            ArrayLeaders[i][j+1]=red.getID();
            ArrayLeaders[i][j+2]=red.getX();
            ArrayLeaders[i][j+3]=red.getY();
            
            //Information about the ant's Leader
             ArrayLeaders[i][j+4]=0; //we are adding red ants
            ArrayLeaders[i][j+5]= red.getID();
            ArrayLeaders[i][j+6]= red.getX();
            ArrayLeaders[i][j+7]= red.getY();
            
            i++; // to go to the next column for the next ant
            
        }
        
        //Now with the black ants
        // the i shows the next row and j still stays 0
       
        for(BlackAnt black : blackAnts)
        {
            //Information about the ant
            ArrayLeaders[i][j]=1; //we are adding black ants
            ArrayLeaders[i][j+1]=black.getID();
            ArrayLeaders[i][j+2]=black.getX();
            ArrayLeaders[i][j+3]=black.getY();
            
            //Information about the ant's Leader
             ArrayLeaders[i][j+4]=1; //we are adding black ants
            ArrayLeaders[i][j+5]=black.getID();
            ArrayLeaders[i][j+6]=black.getX();
            ArrayLeaders[i][j+7]=black.getY();
            
            i++; // to go to the next column for the next ant
            
        }
        
     /*   for(int h=0;h<Size2;h++)
        {
            for(int f=0;f<8;f++)
            {
              System.out.println("["+h+","+f+"]="+ArrayLeaders[h][f]);
            }
        }
       */ 
        
   
        /*now i am gonna create a ArrayList in witch i will save objects of NeweEdge*/
        ArrayList<NewEdge> AllEdges = new ArrayList<>();
        
        /*For each ant I will create an object of NewEdge.
        /*First for each RED ant I will create an edge for every other Red ant*/ 
        
        for(RedAnt redAnt1 : redAnts) 
        {
            /*the information for our red ant*/
            int color1=0;
            //System.out.println("color1="+color1);
            int ID1=redAnt1.getID();
          //  System.out.println("ID1="+ID1);
            double X1 = redAnt1.getX();
        //    System.out.println("X1="+X1);
            double Y1 = redAnt1.getY();
         //   System.out.println("Y1="+Y1);
            
            /*the information for each other red ant that our ant will connect with*/
                for(RedAnt redAnt2 : redAnts)
                   {
                      int color2=0;
                 //System.out.println("color2="+color2);
                      int ID2=redAnt2.getID();
                 //System.out.println("ID2="+ID2);
                      double X2=  redAnt2.getX();
                  //System.out.println("X2="+X2);
                      double Y2 = redAnt2.getY();
                 //System.out.println("Y2="+Y2);
            
                      /* i am finding the distace between them*/
                      double Distance1 = redAnt1.getDistanceFrom(redAnt2); 
                      
                     /*I will create the NewEdge object and then add it to my ArrayList with the Edges*/
                     NewEdge Edge1 = new NewEdge(Distance1,color1,X1,Y1,ID1,color2,X2,Y2,ID2);
            
                     /*I am checking if the distance between the two red ants is 0 or not. If not then add the edge or
                     else don't cause we are talking about the same ant.*/
                     if(Distance1!=0)
                        {
                         AllEdges.add(Edge1);
                        }
                    }
            
               
              /*the information for each other black ant that our ant will connect with*/
                for(BlackAnt blackAnt : blackAnts)
                    {
                      int color3=1; 
               //System.out.println("color3="+color3);
                      int ID3 = blackAnt.getID();
                //System.out.println("ID3="+ID3);
                      double X3 = blackAnt.getX();
                //System.out.println("X3="+X3);
                      double Y3 = blackAnt.getY();
                //System.out.println("Y3="+Y3);
            
                      /* i am finding the distace between them*/
                      double Distance2 = redAnt1.getDistanceFrom(blackAnt);
                          
                     /*I will create the NewEdge object and then add it to my ArrayList with the Edges*/
                      NewEdge Edge2 = new NewEdge(Distance2,color1,X1,Y1,ID1,color3,X3,Y3,ID3);
                      /*add the edge*/
                      AllEdges.add(Edge2);
            }
        }
            
        /*Now I will do the same for each Black ant for each other black ant*/
        for(BlackAnt blackAnt1 : blackAnts)
           {
             /*the information for our black ant*/
              int color4=1;
       //System.out.println("color4="+color4);
              int ID4 = blackAnt1.getID();
      //System.out.println("ID4"+ID4);
              double X4 = blackAnt1.getX();
       //System.out.println("X4="+X4);
              double Y4 = blackAnt1.getY();
       //System.out.println("Y4="+Y4);
              
             /*the information for each other black ant that our ant will connect with*/
                for(BlackAnt blackAnt2 : blackAnts)
                    {
                        int color5=1; 
               //System.out.println("color5="+color5);
                        int ID5 = blackAnt2.getID();
             //System.out.println("ID5"+ID5);
                        double X5 = blackAnt2.getX();
           //System.out.println("X5="+X5);
                        double Y5 = blackAnt2.getY();
          //System.out.println("Y5="+Y5);
            
                        /* i am finding the distace between them*/
                        double Distance3 = blackAnt1.getDistanceFrom(blackAnt2);
                        
                          /*I will create the NewEdge object and then add it to my ArrayList with the Edges*/
                         NewEdge Edge3 = new NewEdge(Distance3,color4,X4,Y4,ID4,color5,X5,Y5,ID5);
            
                         /*I am checking if the distance between the two black ants is 0 or not. If not then add the edge or
                          else don't cause we are talking about the same ant.*/
                         if(Distance3!=0)
                               {
                                  AllEdges.add(Edge3);
                               }
                    }              
     /*I am not gonna connect again the black with red ones causes the results will be the same as the red with black, and
             those edges are already in the ArrayList AllEdges so there is no need to add them again.*/
        }           
                   
        
          /* for(NewEdge edge :AllEdges)
           {
            System.out.println("ID1="+edge.getID1()+" Color1="+edge.getColor1()+" ID2="+edge.getID2()+" Color2="+edge.getColor2());
           }*/
        
        
    /*Now that we have all the edges we need to sort the arraylist with the Edges from the smallest to the biggest distance*/
        Collections.sort(AllEdges,new AComparator());
          
           /* for(NewEdge edge :AllEdges)
           {
            System.out.println("ID1="+edge.getID1()+" Color1="+edge.getColor1()+" ID2="+edge.getID2()+" Color2="+edge.getColor2()+" Distance = " + edge.getDistance());
           }*/
        
         
        /*Now we are going  to find the edges that are going to be used and put them in an other ArrayList*/
        ArrayList<NewEdge> EdgeWeNeed = new ArrayList<>();
          
        /*For every edge we are going to take the information for its begigging and ending ants and compare their leaders with
        the help of the ArrayLeaders.*/         
        for(NewEdge edge : AllEdges)
            {
               int IDBeggining = edge.getID1();
          //System.out.println("IDBegin="+IDBeggining);
               int ColorBeggining = edge.getColor1();
       //System.out.println("ColorBegin="+ColorBeggining);
               int IDEnding = edge.getID2();
        //System.out.println("IDEnd="+IDEnding);
               int ColorEnding = edge.getColor2();
        //System.out.println("ColorEnd="+ColorEnding);
         
               int check1=0; 
               int a=0; // is going to be used for changing the row and going for the next ant in the ArrayLeaders
               int b=0; // is going to be used for moving among the columns
               int ant1=1111; //is going to keep the line in witch we found our begging ant(intialized)
             
            
              /* we are going to search for our beggining ant in the ArrayLeaders*/
              while(check1==0) 
              {
                  if(ArrayLeaders[a][b] == (double)ColorBeggining)
                  {
                      if(ArrayLeaders[a][b+1] == (double)IDBeggining)
                      {
                          check1=1;
                          ant1=a;
                //System.out.println("ant1="+ant1);
                      }
                  }
                  a++;
              }
          
              /*now we are going to look for the ending ant*/
              int check2=0;
              int c=0; // the same with a
              int d=0; //the same with b
              int ant2=1111;//is going to keep the line in witch we found our ending ant (intialized)
             
             while(check2==0)
                   {
                     if(ArrayLeaders[c][d] == (double)ColorEnding)
                        {
                         if(ArrayLeaders[c][d+1] == (double)IDEnding)
                           {
                            check2=1;
                            ant2=c;
                    //System.out.println("ant2="+ant2);
                           }
                        }
                        c++;
                    }
   
          /*Now that we found them we are going to compare their leaders. Basically we are going to
           compare the (x,y) of their leaders.if they are different then we will enter 
           the edge in the EdgesWeNeed ArrayList. If they are the same then that means that they belong to the same set
           and they will create a circle.*/
          
             
             /*if they have the same x then check their y*/
                if( ArrayLeaders[ant1][6] == ArrayLeaders[ant2][6])
                  {
              //System.out.println("ArrayLeaders[ant1][6]="+ArrayLeaders[ant1][6] + "ArrayLeaders[ant2][6]="+ArrayLeaders[ant2][6]);
                  /*if they have the same y then they are in the same set, so no edge added*/
                   if(ArrayLeaders[ant1][7] == ArrayLeaders[ant2][7])
                     {
                    //System.out.println("ArrayLeaders[ant1][7]="+ArrayLeaders[ant1][7] + "ArrayLeaders[ant2][7]="+ArrayLeaders[ant2][7]);
                    //System.out.println("They are in the same set already.");
                     }
             /*if they have different y then they aren't in the same ser. so add the edge alog with the changes*/
                   else
                  /*I am going to change the endings ant's leader's information*/
                   {
               //System.out.println("They don't have the same leaders, so they aren't in the same set.");
                     double X=ArrayLeaders[ant2][6];
                     double Y=ArrayLeaders[ant2][7];
                     
                     /*look in the ArrayLeader for every ant that has the same leader as ant2's previous leader(before the new change) and replace its leader with ant1's leader*/
                     for(int g=0;g<Size2;g++)
                     {
                         if(ArrayLeaders[g][6] == X)
                         {
                             if(ArrayLeaders[g][7]==Y)
                             {
                                 ArrayLeaders[g][4]=ArrayLeaders[ant1][4];
                            //System.out.println(" ArrayLeaders[g][4]="+ArrayLeaders[g][4]+" ArrayLeaders[ant1][4]=" +ArrayLeaders[ant1][4]);
                                 ArrayLeaders[g][5]=ArrayLeaders[ant1][5];
                          //System.out.println(" ArrayLeaders[g][5]="+ArrayLeaders[g][5]+" ArrayLeaders[ant1][5]=" +ArrayLeaders[ant1][5]);
                                 ArrayLeaders[g][6]=ArrayLeaders[ant1][6];
                        //System.out.println(" ArrayLeaders[g][6]="+ArrayLeaders[g][6]+" ArrayLeaders[ant1][6]=" +ArrayLeaders[ant1][6]);
                                 ArrayLeaders[g][7]=ArrayLeaders[ant1][7];
                         //System.out.println(" ArrayLeaders[g][7]="+ArrayLeaders[g][7]+" ArrayLeaders[ant1][7]=" +ArrayLeaders[ant1][7]);
                             }
                         }
                     }
                     EdgeWeNeed.add(edge);
                     
                     
                       /*for(int h=0;h<Size2;h++)
                          {
                            for(int f=0;f<8;f++)
                               {
                                System.out.println("["+h+","+f+"]="+ArrayLeaders[h][f]);
                               }
                          }*/
                   }
                  }
           /*if they have different x then they are in different sets, so add the edge along with the changes*/
                else
                   {
                    // System.out.println("They don't have the same leaders, so they aren't in the same set.");
                     double X=ArrayLeaders[ant2][6];
                     double Y = ArrayLeaders[ant2][7];
                     for(int g=0;g<Size2;g++)//look in the ArrayLeader for every ant that has the same leader as ant2 and replace its leader with ant1's leader
                     {
                         if(ArrayLeaders[g][6] == X)
                         {
                             if(ArrayLeaders[g][7]==Y)
                             {
                                 ArrayLeaders[g][4]=ArrayLeaders[ant1][4];
                              //   System.out.println(" ArrayLeaders[g][4]="+ArrayLeaders[g][4]+" ArrayLeaders[ant1][4]=" +ArrayLeaders[ant1][4]);
                                 ArrayLeaders[g][5]=ArrayLeaders[ant1][5];
                            //     System.out.println(" ArrayLeaders[g][5]="+ArrayLeaders[g][5]+" ArrayLeaders[ant1][5]=" +ArrayLeaders[ant1][5]);
                                 ArrayLeaders[g][6]=ArrayLeaders[ant1][6];
                            //     System.out.println(" ArrayLeaders[g][6]="+ArrayLeaders[g][6]+" ArrayLeaders[ant1][6]=" +ArrayLeaders[ant1][6]);
                                 ArrayLeaders[g][7]=ArrayLeaders[ant1][7];
                              //   System.out.println(" ArrayLeaders[g][7]="+ArrayLeaders[g][7]+" ArrayLeaders[ant1][7]=" +ArrayLeaders[ant1][7]);
                             }
                         }
                     }
                     EdgeWeNeed.add(edge);
                     
                     
                 /*   for(int h=0;h<Size2;h++)
                          {
                              for(int f=0;f<8;f++)
                                 {
                                System.out.println("["+h+","+f+"]="+ArrayLeaders[h][f]);
                                 }
                          }*/
                   }
            }
         
          /*Now that I have our ArrayList ready with the edges that are going to be used I am going to make the Array
          that we want to return*/
          
          int Size3 = EdgeWeNeed.size();
      //System.out.println("Size3="+Size3);
          
          int ArrayEnd[][] = new int[Size3][4];
          int m=0; //it will counts the rows
          for(NewEdge edges : EdgeWeNeed )
          {
              int Color1= edges.getColor1();
              int Color2 = edges.getColor2();
              int ID1= edges.getID1();
              int ID2 = edges.getID2();
              
              ArrayEnd[m][0]=ID1;
              ArrayEnd[m][1]=Color1;
              ArrayEnd[m][2]=ID2;
              ArrayEnd[m][3]=Color2;
         
              m++; 
          }
          
        return ArrayEnd;
        
    }

    
    
    
    
    
    
    
    /**
     * In this method I am going to materialize StableMatchigs.
     * I am taking the size of the reAnts, to find how many red ants i have.
     * Then I am creating a HashMap in witch i have as keys the ID of each red ant and as their values an ArrayList
     * with Edge objects.
     * This ArrayList ,named BlackEdges, save Edge objects, witch hold the ID of the red ant(key) , the ID of the
     * black ant and the distance between them. The ArrayList will have as many objects as the number of the black
     * ants, cause basically I am saving the edges between a red with each other black.
     * Then I am creating a 2D Array(MatchingArray) with rows=number of all ants and 5 columns. At the first column i am saving the
     * color of the starting ant, at the second column it's ID, at the third column the color of the finishing ant,
     * at the forth column it's ID and at the last column the distance(weight) between them. I am adding first the red ants
     * and after the black ants. The columns 2 and 3  (with the color and ID of the finishing ant) basically holds the
     * information about the match of the ant whose information are in the 0 and 1 column.
     * So because at first the ants doesn't have matches we are adding at the third and forth column -1 and 0 to the last column
     * for the weight.
     * Now about the basic idea of creating matchings.
     * According to StableMarriage i need to have the preferences of both red and black ants.
     * But here , because the matchings will be create based on the weights (if the new match gives less weight that the one i have 
     * i match with the new one) i didn't consider the preferences of the black ants.
     * What i mean is that I took for each red ant it's ArrayList with the Edge objects with each black ant and sorted it with the
     * help of a comparator based on the weights, so the first object has the less weight. With that way i have the preference
     * for each ant. Now as long as the last column of the red ant's row in the MatchingArray is 0(it doesn't have a match) 
     * i will go to find one for it. So i am checking the first object of the ArrayList. I am taking the ID of the back ant(getTo)
     * and the distance between them. I am looking for the black ant in the MatchingArray based on it's ID.(here i am starting
     * to look for it in the half of the Array, cause at the first half of the Array are saved the red ants and at the rest of it the 
     * black ants. If the last column of the row with the black ant is 0 then we match them, we put the information of the black
     * ant at the thesis for the match ant at the red ant's row and the information of the red ant at the thesis for the match ant at
     * the black ant's row along with their distance. But if the the black ant has already a match, we compare the two distances.
     * The distance that is saved at the last column of the black ant's row and the distance that we took from the Edge object at
     * the beginning. If the distance of our red ant is smaller than the distance that the black already has, then we replace
     * the information about the previous black ant's match with the information of our red ant along with their distance, AND we
     * take the ID of the previous red ant from the thesis before we replace it with our red ants ID and look for it in the MathingArray.
     * Then we place -1 at the thesis for the color and ID of it's match and 0 at the thesis for the distance. Now if the distance of our
     * red ant was bigger then we are getting the next Edge object of our ArrayList and do the same checking.
     * So as you saw i didn't need the preferences of the black ants.
     * As long as there is 0 in any thesis of the last column of the first half of the ArrayMatching
     * I am going to check that red ant to find it a match. When there will be no 0 , then all the ants will have a match.
     * At the end I am taking the ID's of each red ant and the ID of it's match and add it to the Array that i am returning.
     * 
     * 
     * @param redAnts
     * @param blackAnts
     * @return 
     */
    @Override
     public int[][] findStableMarriage(ArrayList<RedAnt> redAnts, ArrayList<BlackAnt> blackAnts) 
     {
         
         int Size1= redAnts.size();
      //System.out.println("Size1="+Size1);
         int Size2 = 2*Size1;
      //System.out.println("Size2="+Size2);
         
         
         /*I am going to create a HashMap with rows=number of red ants.
         The keys are the red ants' IDs and their values an ArrayList with Edge Objects.*/
         HashMap<Double,ArrayList<Edge>> Matchings = new HashMap<Double,ArrayList<Edge>>();
         
         for(RedAnt red : redAnts)
            { /*i will take the (x,y) and the ID of our red ant*/
                double XR= red.getX();
                //System.out.println("XR="+XR);
                double YR =red.getY();
              //System.out.println("YR="+YR);
                double IDR = red.getID();
             //System.out.println("IDR="+IDR);
                
                /* For this red ant, I will create an ArraList in witch i will save Edge objects.
                Each Object is the edge between this red ant and each black ant */
               ArrayList<Edge> BlackEdges = new ArrayList<>();
                 for(BlackAnt black : blackAnts)
                   {/*i will take the (x,y) of each black, find the distace between that black and our red ant, 
                       create object Edge and add it to the ArrayList BlackEdges */
                       
                     double XB= black.getX();
                    // System.out.println("XB="+XB);
                     double YB = black.getY();
                   //  System.out.println("YB="+YB);
                     double IDB = black.getID();
                    // System.out.println("IDB="+IDB);
                     double Distance = red.getDistanceFrom(black);
                   //  System.out.println("Distance="+Distance);
                 
                     Edge  NEdge = new Edge((int)IDR,(int)IDB,Distance);
                    // System.out.println("Red:"+NEdge.getFrom()+"   Black"+NEdge.getTo()+"   Distance:"+NEdge.getDistance());
                     
                      BlackEdges.add(NEdge);
                      //System.out.println("SIZE ARRAYLIST: "+BlackEdges.size());
                    }
                    
             
             /*After creating the ArrayList for red ant, we add it as value to our HashMap to the red's ID=key
                 and go to our next red ant.*/
             Matchings.put(IDR,BlackEdges);          
            }
         
       
         
            /* Now i will create a 2D Array in witch i will save for every ant its match and their distance.
            The first column will have the color of the one ant , the second column its ID, the third one the color
            of the second ant, the forth the ID of the second one and the last colum the distance betwween them.*/
            double MatchArray [][]= new double[Size2][5];
             
            int ii=0;
            int j=0;
            int l=0;
            
            for(RedAnt red: redAnts)
            {
                MatchArray[ii][j] = (double)0; // color
                MatchArray[ii][j+1] =(double) red.getID();
                MatchArray[ii][j+2] = (double)-1; // for now cause we don't know its match yet,color
                MatchArray[ii][j+3] = (double)-1; //the same,ID
                MatchArray[ii][j+4] = (double)0; // still no match so distance=0;
                ii++; // next row for the next ant
            }
         
             j=0;
             
            for(BlackAnt black: blackAnts)
               {
                MatchArray[ii][j] = (double)1; //the i the next row after the last red Ant
                MatchArray[ii][j+1]= (double) black.getID();
                MatchArray[ii][j+2] = (double)-1;//color
                MatchArray[ii][j+3] = (double)-1;//ID
                MatchArray[ii][j+4] =(double)0;// still no match so distance=0;
                ii++;
               }
         
        // System.out.println(Arrays.deepToString(MatchArray));
         
         
            /*now i will start with finding for each red ant it's match. For that i will
            see the last column of MatchingArray for the red ants only. If 0 then i will have 
            to find it a match!*/
            
            int HasMatch=0;
            int i=0;
            
            while(HasMatch==0)
                 {
                   if(MatchArray[i][4]==0.0)/* if the distance of the red ant in the i row is 0, that means that is has no
                                          match, so i need to find one for it*/
                      {
                        double IDRed1 = MatchArray[i][1]; /*get the ID of the red ant*/
              // System.out.println("IDRED1="+IDRed1);
          
                        ArrayList<Edge> ListOfEdges = new ArrayList<>(); /*Create an ArrayList with objects from the Class Edge*/
                        ListOfEdges=Matchings.get(IDRed1); /*add to ListOfEdges the ArrayList with the Edge Objects*/
                     
                       
                     /*    for(Edge ed : ListOfEdges)
                         {
                             System.out.println("HERE:"+ed.getFrom());
                         }*/
                        
                         /*I am sorting it based on the distances*/
                    Collections.sort(ListOfEdges,new BComparator());
                   
                         Edge NEdge2 = new Edge(); /*create an object of Class Edge*/
                         
                         int check=0;
                         int  NumberOfObject=0; /*because we are going to start with the first object of the arraylist ListOfEdges*/
                       
                         /* we need to find a match for our red ant. so we are goind to chack each black ant in the ListOfEdges
                         until we find our match*/
                         while(check==0)
                              {
                         //System.out.println("Number of object ="+ NumberOfObject);
                                  
                                NEdge2 = ListOfEdges.get(NumberOfObject); /*get the fist object in the ListOfEdges*/
                                double Distance1 = NEdge2.getDistance(); /*get the distance between our red ant and this black ant*/
                        //System.out.println("DISTANCE1="+Distance1);
                                double IDBlack = (double)NEdge2.getTo(); /*Get the black ant's ID (cause i have stored their IDs in this object along with their distance*/
                             
                            
                          //System.out.println("idblack="+IDBlack);
                                /*Now i am going to look for this black ant in the MatchArray based on its ID and see if it has a match*/
                                 int check2=0;
                                 int row=Size1; /* counts the rows, and it will start from the row Size1 cause the black ants are all under the red ants*/
                              // System.out.println("match1="+MatchArray[row][1]);
                                 /*we have the black ant that the red offered to marry, so we are going to check if it can ''marry'' it.*/
                                 while(check2==0)
                                      {
                                  // System.out.println("CHECK 2="+ check2);
                                   //System.out.println("row="+row);
                                    // System.out.println("match="+MatchArray[row][1]);
                                       if(MatchArray[row][1]==IDBlack)/* if you find the black ant*/
                                          {
                                           // System.out.println("match="+MatchArray[row][4]);
                                            if(MatchArray[row][4]==0.0) /*and if distace=0 , it has no match*/
                                              {/*then we are going to match those two ants.*/
                                               /*first we add the red and to the black ant*/
                                     //System.out.println(" distance is 0");
                                               MatchArray[row][2]=(double)0;/*it's the color of the ant*/
                                               MatchArray[row][3]=IDRed1;/*The ID of our red ant*/
                                               MatchArray[row][4]=Distance1; /*the distance that we found between those 2*/
                                         
                                               /*and now we add the black ant to the red ant*/
                                               MatchArray[i][2]=(double)1;/*it's the color of the ant*/
                                               MatchArray[i][3]=IDBlack;/*The ID of te black ant*/
                                               MatchArray[i][4]=Distance1; /*the distance that we found between those 2*/
                                               
                                               check2=1;/*get out of while cause we found and did what we wanted. Red has a match*/
                                               check=1; /*Also get out and from the previous while cause we don't need to check the rest 
                                                          of the black ants in the ListOfEdges*/
                                               
                                        //System.out.println(Arrays.deepToString(MatchArray));
                                               }
                                            else/* that means taht the black ant has a match. so we need to see whom from the two red ats will prefer*/
                                               {
                                               //   System.out.println("distance is not zero");
                                                  double Distance2 = MatchArray[row][4];/*get the distance that the black ant already has with the other red ant*/
                                                  if(Distance1>Distance2)/*if the distance of our ant form the black ant is bigger that eith the other one*/
                                                    {
                                              //System.out.println("red1 > red2");
                                                      check2=1;/*get out of the while cause we foud our black ant and we don't need to look further
                                                               this black ant won't accept, so you need to chack the next black ant of the ListOfEdges*/
                                                      NumberOfObject++; /*we are going for the next black ant in the ListOfEdges*/
                                                      //System.out.println(Arrays.deepToString(MatchArray));
                                                    }
                                                  else/* if our distance is smaller than the other one*/
                                                    {/*then we need to do 3 things. 1. chage the information about the second red ant in the MatchArray so it won't have a match anymore and 
                                                                                    2. and 3. exchange information in the MatchArray of pur red ant and this black ant*/
                                                       double IDRedEx=MatchArray[row][3];/*we are getting its ID so we can find it in the MatchArray*/
                                              //System.out.println("red1<red2");
                                                       /*now we are going to exchange information*/
                                                       MatchArray[row][2]=(double)0;/*it's the color of the ant*/
                                                       MatchArray[row][3]=IDRed1;/*The ID of our red ant*/
                                                       MatchArray[row][4]=Distance1; /*the distance that we found between those 2*/
                                         
                                                       /*and now we add the black ant to the red ant*/
                                                       MatchArray[i][2]=(double)1;/*it's the color of the ant*/
                                                       MatchArray[i][3]=IDBlack;/*The ID of te black ant*/
                                                       MatchArray[i][4]=Distance1; /*the distance that we found between those 2*/
                                                       
                                                       /* now we are going to look for the other red ant in the MatchArray to cahnge its information of its match too*/
                                                       int h=0;
                                                       int count=0;
                                                       while(h==0)
                                                       {
                                                           if(MatchArray[count][1]==IDRedEx)/*if you find the ant*/
                                                             {/*change its match's information*/
                                                                MatchArray[count][2]=(double)-1;
                                                                MatchArray[count][3]=(double)-1;
                                                                MatchArray[count][4]=(double)0;
                                                                h=1;
                                                             }
                                                            count++;
                                                       }
                                                      
                                                       check2=1;/*get out of while , cause we found the black ant and we don't need to look further*/
                                                       check=1;/*get out of the previous while too, cause we found our match*/
                                                     //  System.out.println(Arrays.deepToString(MatchArray));
                                                    }//end if (it has match the black , check the distances)
                                             
                                               } //end if (it has match or not the black)
                                            
                                           }//end if (looking for the black ant)
                                       row++;
                                       }//end while ( looking trough the array to look for the black ant)
                           
                                   /*look for the black ant to the next row*/
                             } // end while (taking each black ant from the arraylist to find our red's match
                        }// end if (the red ant of this row has or not match)
                 

                   /* else if the red ant we chacked has distance(it has match) we go to the next red ant*/
                 
                       j=0;
                       for(int k=0;k<Size2;k++)/* i am going to check the colunm 4 to see if there are 0, if yes that means that
                                                  there are ants with no match.*/
                          {
                              
                             if(MatchArray[k][4]==0.0)
                                {
                         //System.out.println("is 0");
                                   j++; /*it counts how many 0 exists in the last colunm (it contains the distance from the match*/
                                }   
                          }
                       
                       if(j!=0) /*that means that there are ants with no match*/
                       {
                           HasMatch=0; /* so we need to keep on finding matches*/
                       }
                       else /*j=0, means that all the ants has a match*/
                       {
                           HasMatch=1; /*so we are stoping the while*/
                       }
                       
                       i++; /*going to the next ant (row)*/
                       if(i==Size1)/*because the MatchArray has the red ants first from 0 to Size1-1 and then the black ants and 
                                     i only want to see if the red ants have or not match, if i=Size1, witch is a black ant, i need
                                     to zero it so i can recheck all the red ants to see if any red ant has o match.*/
                          {
                             i=0;
                          }
                       
                 } /*  out of the while, that means that all the ants have matches*/
            
            
           int[][] ArrayReturn = new int[Size1][2];
           
           for(int f=0;f<Size1;f++)
           {
               ArrayReturn[f][0]=(int)MatchArray[f][1];
               ArrayReturn[f][1]=(int)MatchArray[f][3];
           }
                       
   
        return ArrayReturn;
    }

     
     
     
     
     
     
     
     
     /**
      * In this method i am going to materialize CoinChange.
      * I am taking the given array with the weights of each seed(KindOfSeeds) and the capacity of the red ant.
      * I am not creating an array to save the seed from 0 to Capacity+1. I am taking that it is known that each thesis 
      * of the other arrays based on its number it assigns to the same number of seeds. example the thesis 10 is talking about 10 seeds.
      * 1.TheBestSolution. It's an Array sizes Capacity+1 (cause it starts with 0 and the capacity it self wouldn't be counted) , 
      * in witch i will save the best solution of each seed from zero to capacity.
      * 2.TheSeedThatWasUsed. It's an Array size Capacity+1, in witch i will save the seed that was used to find its best solution. That 
      * seed will be one of the five weights witch are given.
      * 3.Veltisto. It's a 2D Array size 5 X 2 (first column is saved the five weights and the second the solution that each one gives), that will be used to save the solution that gives each of the five seeds for each seed
      * from 0 to Capacity. But at first the second column has zeros. With this Array i will find the best one and add the seed to the TheSeedThatWasUsed at the thesis of the
      * seed i was checking, and i will add the solution it self at the TheBestSolution again at the thesis of the seed that i was checking.
      * example if i was checking the seed 50, i will put the information i need to the thesis 50 of the other two Arrays.
      * At the comments after i am using the word 'seed' instead of 'weight'.
      * To find the solution that gives each weight for the seed that i am checking,i am taking out of the seed the weight 
      * and then i am looking for the best solution of the remaining in the TheBestSolution. Then i am adding 1 and
      * save the solution to the Veltisto at the second column of the row of the weight that i used.
      * While i am checking for the solution that each weight gives for the seed I am checking, if the weight i am using
      * is bigger than the seed (the numbers) than i am adding to the Veltisto the number 1000000 1.because this weight
      * can not be used and 2.so when i am going to find the smallest number saved in the second column in the Veltisto (best solution) this
      * won't bother. (generally the way i am finding the solution is basically dynamic programming)
      * After finding the best solution, i need to find and witch of the five seeds gave it. so i a looking the best solution
      * in Veltisto and after finding it's row i am taking the number that is saved in the first column of this row and adding at the
      * TheSeedThatWasUsed and the best solution at the TheBestSolution like i said earlier.
      * After finishing with the first two Arrays and now they are full i am going to find how many of the given weight(seeds)
      * used for the best solution of the Capacity.
      * So i made an other array like Veltisto, NumberOfSeeds in witch i will keep the number of times that each seed/weight
      * was actually used. But for now this column has zeros.
      * So i am going to the last thesis of TheSeedThatWasUsed, i am taking the number that is saved and look for it in the
      * first column of NumberOfSeeds. Then using a counter i increase it by one. I take the weight out of the capacity
      * and go to the next thesis=capacity-weight. I do the same thing until the capacity goes zero.
      * At the end i create a array in witch i save the second column of NumberOfSeeds and return it.
      * 
      * the only thing that you have to remember is that each thesis is and the number of seeds that i am checking
      * each time at the arrays TheBestSolution and TheSeedThatWasUsed.
      * 
      * 
      * @param redAnt
      * @param blackAnt
      * @return 
      */
    @Override
    public int[] coinChange(RedAnt redAnt, BlackAnt blackAnt) 
    {
      
        /*i will a clone of the array with the 5 seeds/weights */
        int[] KindOfSeeds = blackAnt.getObjects().clone();
        
     
        for(int po=0;po<5;po++)
        {
            System.out.println("Seeds[]="+KindOfSeeds[po]);
        }
         
        /*i will get the capacity of the red ant.*/
        int Capacity = redAnt.getCapacity();
     //System.out.println("Capacity ="+ Capacity);

       /*It will keep the best solution for every seed from 0 to Capacity+1*/
        int[] TheBestSolution= new int[Capacity+1];
        
        /*the Array will keep the seed that every seed in the ArrayCheck[] used as a start*/
        int[] TheSeedThatWasUsed= new int[Capacity+1];

        /*i will use a 2D array to put each weight of 5 and the solution it gives for each seed in the ArrayCheck*/ 
        int[][] Veltisto = new int[5][2];
        
        /*we are putting in the first column the five seeds*/
        for(int d=0;d<5;d++)
        {
            Veltisto[d][0]=KindOfSeeds[d];
          //  System.out.println("Veltisto[]="+Veltisto[d][0]);
        }
        
        
        int Ypoloipo;
        int Seed;
        
        for(int i=0;i<Capacity+1;i++) /** for every seed from 0 to Capacity*/
           {
            if(i==0)
              {
               TheBestSolution[i]=0;
              }
            else
              {
                  for(int k=0;k<5;k++)// for each of the 5 seeds
                      {
                         Seed=KindOfSeeds[k]; //we keep the seed
                       // System.out.println("Seed="+Seed);
                         Ypoloipo=i-Seed; // we are taking the seed from the amount of seeds that we have(i)
                        // System.out.println("Ypoloipo="+Ypoloipo);
                         
                         if(Ypoloipo==0)
                           {
                         //System.out.println("the best solution of ypoloipo is ="+TheBestSolution[Ypoloipo]);
                             Veltisto[k][1]=1; // we are using 1 seed
                         //System.out.println("Veltisto1[][]="+Veltisto[k][1]);
                           }
                          else if(Ypoloipo>0)
                           {
                            //System.out.println("the best solution of upoloipo is="+TheBestSolution[Ypoloipo]);
                             Veltisto[k][1] = 1 + TheBestSolution[Ypoloipo];
                            //System.out.println("Veltisto2[][]="+Veltisto[k][1]);
                           }
                         else
                          {
                              Veltisto[k][1] = 1000000;   /* i am putting this umber cause it won't be any near the other numbers
                                                          so it won't bother the finding of min afterwads*/
                              // System.out.println("Veltisto2[][]="+Veltisto[k][1]);
                          }
                      }
              
        
                    /*i am going to find the best soloution*/
                    int min= Veltisto[0][1];
            
                     for(int g=0;g<5;g++)
                        {
                           if(min>Veltisto[g][1])
                             {
                               min=Veltisto[g][1];
                             }
                       }
                     
                     TheBestSolution[i]=min; /* we are adding the number of seeds that was used for i amount of seeds*/
                     //System.out.println("Min="+min);
                       
                      /*Now we need to find witch of the five seeds gave the best solution.
                        so we are going to look for the min in the Veltisto witch 
                        has the seed and the solution that it gave earlier.
                        Once we find it we add it to the TheSeedThatWasUsed[i]*/
                      if(Veltisto[0][1]==min)
                        {
                         TheSeedThatWasUsed[i]=Veltisto[0][0];
                        }
                      else if (Veltisto[1][1]==min)
                        {
                         TheSeedThatWasUsed[i]=Veltisto[1][0];
                        }
                      else if(Veltisto[2][1]==min)
                        {
                         TheSeedThatWasUsed[i]=Veltisto[2][0];
                        }
                      else if(Veltisto[3][1]==min)
                        {
                         TheSeedThatWasUsed[i]=Veltisto[3][0];
                        }
                      else if(Veltisto[4][1]==min)
                       {
                         TheSeedThatWasUsed[i]=Veltisto[4][0];
                       } 
                      
                      //System.out.println("the seed that was used is "+ TheSeedThatWasUsed[i]);
               }
           }
      
        /*now that we made the arrays we can go and find the number of each seed that was used.*/
        int NumberOfSeeds[][] = new int[5][2]; /*it will keep the numbers of each of the five seeds that was
                                                 used to create the amount i. */
                                                                                
        /*we are putting the kind of seeds in the first column the seeds and 0 to the second column*/
       for(int t=0;t<5;t++)
        {
            NumberOfSeeds[t][0]=KindOfSeeds[t];
            NumberOfSeeds[t][1]=0;
        }
               
                  /*First we are going to take the seed that was used for the Capacity of RedAnt and look for it
                    in the first column of NumberOfSeeds.Once we find it we are going to increase by 1 the 
                    index of its second column seat*/
                   
                if(TheSeedThatWasUsed[Capacity]== NumberOfSeeds[0][0])
                  {
                    NumberOfSeeds[0][1]=NumberOfSeeds[0][1]+1;
                  }
                else if(TheSeedThatWasUsed[Capacity]==NumberOfSeeds[1][0])
                  {
                    NumberOfSeeds[1][1]=NumberOfSeeds[1][1]+1;
                  }
                else if(TheSeedThatWasUsed[Capacity]==NumberOfSeeds[2][0])
                  {
                    NumberOfSeeds[2][1]=NumberOfSeeds[2][1]+1;
                  }
                else if(TheSeedThatWasUsed[Capacity]==NumberOfSeeds[3][0])
                 {
                    NumberOfSeeds[3][1]=NumberOfSeeds[3][1]+1;
                 }
                else if(TheSeedThatWasUsed[Capacity]==NumberOfSeeds[4][0])
                 {
                    NumberOfSeeds[4][1]=NumberOfSeeds[4][1]+1;
                 }
                
       /* for(int e=0;e<5;e++)
         {
          System.out.println("Numberofsedd[][]="+NumberOfSeeds[e][1]);
         }*/
             
            
          int num=TheSeedThatWasUsed[Capacity]; //Capacity of the red
          Capacity = Capacity-num;// we are taking the seed out of the capacity
       
          while(Capacity!=0) // while we haven't finished with the whole campacity
               {
                 //go to the new seat (as the amount of seeds are the same as the number of the seat)
                num = TheSeedThatWasUsed[Capacity];// find the seed that it used
                if(num==NumberOfSeeds[0][0])
                   {
                    NumberOfSeeds[0][1]=NumberOfSeeds[0][1]+1;
                   }
                else if(num==NumberOfSeeds[1][0])
                  {
                    NumberOfSeeds[1][1]=NumberOfSeeds[1][1]+1;
                  }
                else if(num==NumberOfSeeds[2][0])
                  {
                    NumberOfSeeds[2][1]=NumberOfSeeds[2][1]+1;
                  }
                else if(num==NumberOfSeeds[3][0])
                  {
                    NumberOfSeeds[3][1]=NumberOfSeeds[3][1]+1;
                  }
                else if(num==NumberOfSeeds[4][0])
                  {
                    NumberOfSeeds[4][1]=NumberOfSeeds[4][1]+1;
                  }
                
                Capacity=Capacity-num; //take it out again       
           }
          
          /* I will create the array that i am going to return and add to it the second column of NumberOfSeeds*/
          int[] FinalNumber = new int[5];
          for(int f=0;f<5;f++)
          {
              FinalNumber[f]=NumberOfSeeds[f][1];
          }
        
       /* for(int gj=0;gj<5;gj++)
        {
          //  System.out.println("Final[] = "+ FinalNumber[gj]);
        }*/
       
        return FinalNumber;
       
    }
    
    
   
    private static void checkParameters(String[] args) {
        if (args.length == 0 || args.length < 2 || (args[1].equals("0") && args.length < 3)) {
            if (args.length > 0 && args[1].equals("0") && args.length < 3) {
                System.out.println("3rd argument is mandatory. Represents the population of the Ants");
            }
            System.out.println("Usage:");
            System.out.println("1st argument: name of filename");
            System.out.println("2nd argument: 0 create random file, 1 input file is given as input");
            System.out.println("3rd argument: number of ants to create (optional if 1 is given in the 2nd argument)");
            System.exit(-1);
        }
    }

    /*
    i put them in comments cause i couldn't continue... i had errors at both of them
    */
    
    /*@Override
    public int[][] findStableMarriage(ArrayList<RedAnt> al, ArrayList<BlackAnt> al1) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int[] coinChange(RedAnt redant, BlackAnt ba) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    */
    
 ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////   
    
    
    /*Classes used in MST*/
    
    
    /**
     * Creates objects witch contains the x,y,id and color of each ant that was used to creat an edge
     * and the weight of the edge (distance)
     * 
     * 
     * @param  Distance 
     * @param color1
     * @param X1
     * @param Y1
     * @param ID1
     * @param color2
     * @param X2
     * @param Y2
     * @param ID2
     * @
     */
    public static class NewEdge
    {

        int color1;
        double X1;
        double Y1;
        int ID1;
        
        int color2;
        double X2;
        double Y2;
        int ID2;
        
        double Distance;
        
        public NewEdge(double Distance, int color1, double X1, double Y1, int ID1, int color2, double X2, double Y2, int ID2) 
        {
      
            this.color1=color1;
            this.ID1=ID1;
            this.X1=X1;
            this.Y1=Y1;
            
            this.color2=color2;
            this.ID2=ID2;
            this.X2=X2;
            this.Y2=Y2;
        
            this.Distance= Distance;
        }
        
        public int getColor1()
        {
            return color1;
        }
        
        public int getID1()
        {
            return ID1;
        }
        
        public double getX1()
        {
            return X1;
        }
        
        public double getY1()
        {
            return Y1;
        }
        
        public int getColor2()
        {
            return color2;
        }
             
        
        public int getID2()
        {
            return ID2;
        }
        
        
        public double getX2()
        {
            return X2;
        }
        
        
        public double getY2()
        {
            return Y2;
        }
        
        
        public double getDistance()
        {
            return Distance;
        }
         
    }
   
   /**
    * It is used to sort the Array AllEdges 
    * @param edge1
    * @param edge2
    * @return
    */
    public class AComparator implements Comparator<NewEdge>
        {
        @Override
        public int compare(NewEdge edge1, NewEdge edge2)
        {
            if(edge1.getDistance()> edge2.getDistance())
            {
                return 1;
            }
            else if( edge1.getDistance()< edge2.getDistance())
            {
                return -1;
            }
            return 0;
        }
        
        }
  
    
    //Classes for StableMarrage
    /**
     * It is used to sort the array ListOfEdges.
     * @param edge1
     * @param edge2
     * @return
     */
    public class BComparator implements Comparator<Edge>
        {
        @Override
        public int compare(Edge edge1, Edge edge2)
        {
            if(edge1.getDistance()> edge2.getDistance())
            {
                return 1;
            }
            else if( edge1.getDistance()< edge2.getDistance())
            {
                return -1;
            }
            return 0;
        }
        
        }
    
}
