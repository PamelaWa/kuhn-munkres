

import sun.awt.image.ImageWatched;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.LinkedList;


public class KM {

    static class Vertex{
        public char partite;  //'x' or 'y'
        public Integer id;    //node ID
        public Integer label = 0;
        public LinkedList<Edge> edges = new LinkedList<Edge>();

        public Vertex(char partiteParam, int idParam){
            partite = partiteParam;
            id = idParam;
        }

        public boolean equals(Object obj){
            if(obj == this) return true;
            if(obj == null) return false;
            if(obj instanceof Vertex){
                Vertex vObj = (Vertex)obj;
                if(this.partite == vObj.partite && this.id == vObj.id)
                    return true;
            }
            return false;
        }

        @Override
        public String toString(){
            return partite + Integer.toString(id) + "-label:" + Integer.toString(label);
        }
    }

    static class Edge{
        public int xId;
        public int yId;
        public int weight = 0;

        public Edge(int xParam, int yParam, int weightParam){
            xId = xParam;
            yId = yParam;
            weight = weightParam;
        }

        public boolean equals(Object obj){
            if(obj == this) return true;
            if(obj == null) return false;
            if(obj instanceof Edge){
                Edge eObj = (Edge)obj;
                if(this.xId == eObj.xId && this.yId == eObj.yId)
                    return true;
            }
            return false;
        }

        @Override
        public String toString(){
            return "(" + xId + "," + yId + "):W" + weight;
        }
    }

    public static LinkedList<Vertex> loadInput(String inputFile){
        LinkedList<Vertex> vertexList = new LinkedList<Vertex>();
        try{
            BufferedReader br = new BufferedReader(new FileReader(inputFile));
            int n = Integer.valueOf(br.readLine());
            for(Integer i = 1; i <= n; i++){ //For each row - Create Vertices
                Vertex xv = new Vertex('x', i);
                Vertex yv = new Vertex('y', i);

                String line = br.readLine();
                String[] arrLine = (line.split("\\s+"));
                for(Integer j = 1; j <= n; j++){    //For each column - Create Edges
                    Edge e = new Edge(i, j, Integer.valueOf(arrLine[j-1]));
                    xv.edges.push(e);
                    yv.edges.push(e);
                }
                vertexList.push(xv);
                vertexList.push(yv);

            }
        }catch (Exception e) {System.out.println("LoadInput() Exception!: " + e);}
        return vertexList;
    }

    public static LinkedList<Edge> loadEdges(LinkedList<Vertex> vertexList){
        LinkedList<Edge> edgeList = new LinkedList<Edge>();
        for(Vertex v : vertexList){
            for(Edge e : v.edges){
                edgeList.add(e);
            }
        }
        return edgeList;
    }

    public static void initializeXlabels(LinkedList<Vertex> vertexList){
        for(Vertex v :  vertexList){
            if(v.partite == 'y') continue;  //X nodes only
            int maxWeight = 0;
            for(Edge e : v.edges){
                if(e.weight > maxWeight) maxWeight = e.weight;
            }
            v.label = maxWeight;
        }
    }

    public static LinkedList<Edge> findTightEdges(LinkedList<Vertex> vertexList){
        LinkedList<Edge> equalityList = new LinkedList<Edge>();
        for(Vertex v : vertexList){
            if(v.partite == 'y') continue; //We only want the 'x' nodes.
            for(Edge e : v.edges){
                int yLabel = getVertex(vertexList, 'y', e.yId).label;
                int xLabel = v.label;
                int edgeWeight = e.weight;
                if(xLabel + yLabel == edgeWeight){
                    equalityList.add(e);
                }
            }
        }
        return equalityList;
    }


    //TODO: might delete this... equalityGraph should be the tight edges, not the vertices.
    public static LinkedList<Vertex> findEquality(LinkedList<Vertex> vertexList){
        LinkedList<Vertex> equalityGraph = new LinkedList<Vertex>();
        //If the tight edge vertices aren't already in equalityGraph, add them.
        for(Edge e : findTightEdges(vertexList)){
            if(getVertex(equalityGraph, 'x', e.xId) == null)
                equalityGraph.add(getVertex(vertexList, 'x', e.xId));
            if(getVertex(equalityGraph, 'y', e.yId) == null)
                equalityGraph.add(getVertex(vertexList, 'y', e.yId));
        }
        return equalityGraph;
    }

    public static Vertex getVertex(LinkedList<Vertex> vertexList, char partiteParam, int idParam){
        for(Vertex v : vertexList){
            if(v.partite == partiteParam && v.id == idParam)
                return v;
        }
        return null;
    }

    public static Edge getEdge(LinkedList<Edge> edgeList, int xParam, int yParam){
        for(Edge e : edgeList){
            if(e.xId == xParam && e.yId == yParam)
                return e;
        }
        return null;
    }



    public static LinkedList<Edge> findAugmentingPath(LinkedList<Vertex> vertexList, LinkedList<Edge> equalityGraph, LinkedList<Edge> matchedEdges){
        LinkedList<Edge> foundEdges = new LinkedList<Edge>();
        LinkedList<Vertex> checkedVertices = new LinkedList<Vertex>();
        LinkedList<Vertex> equalityVertices = new LinkedList<Vertex>();

        //Use the list of tight edges to populate list of vertices for the equality graph
        for(Edge e : equalityGraph){
            Vertex xVertex = getVertex(vertexList, 'x', e.xId);
            Vertex yVertex = getVertex(vertexList, 'y', e.yId);

            if(!equalityVertices.contains(xVertex))
                equalityVertices.add(getVertex(vertexList, 'x', e.xId));
            if(!equalityVertices.contains(yVertex))
                equalityVertices.add(getVertex(vertexList, 'y', e.yId));
        }

        for(Vertex v : equalityVertices){
            foundEdges = checkForUnmatchedEdge(foundEdges, checkedVertices, equalityVertices, matchedEdges, v);
        }
        if(foundEdges.size() >= 3){
            System.out.println("findAugmentingPath().foundEdges: " + foundEdges);
            return foundEdges;
        }
        else return null;
    }

    public static LinkedList<Edge> checkForMatchedEdge(LinkedList<Edge> foundEdges, LinkedList<Vertex> checkedVertices, LinkedList<Vertex> vertexList, LinkedList<Edge> matchedEdges, Vertex toCheck){
        if(checkedVertices.contains(toCheck)) return foundEdges;
        checkedVertices.add(toCheck);

        for(Edge e : toCheck.edges){
            if(getVertex(vertexList, 'x', e.xId).equals(toCheck)) { //Determine whether toCheck is x or y and check the other side.
                foundEdges.add(e);
                return checkForUnmatchedEdge(foundEdges, checkedVertices, vertexList, matchedEdges, getVertex(vertexList, 'y', e.yId));
            }
            else {
                foundEdges.add(e);
                return checkForUnmatchedEdge(foundEdges, checkedVertices, vertexList, matchedEdges, getVertex(vertexList, 'x', e.xId));
            }
        }
        return foundEdges;
    }

    public static LinkedList<Edge> checkForUnmatchedEdge(LinkedList<Edge> foundEdges, LinkedList<Vertex> checkedVertices, LinkedList<Vertex> vertexList, LinkedList<Edge> matchedEdges, Vertex toCheck){
        if(checkedVertices.contains(toCheck)) return foundEdges;
        checkedVertices.add(toCheck);

        for(Edge e : toCheck.edges){
            if(getVertex(vertexList, 'x', e.xId).equals(toCheck)) { //Determine whether toCheck is x or y and check the other side.
                foundEdges.add(e);
                return checkForMatchedEdge(foundEdges, checkedVertices, vertexList, matchedEdges, getVertex(vertexList, 'y', e.yId));
            }
            else {
                foundEdges.add(e);
                return checkForMatchedEdge(foundEdges, checkedVertices, vertexList, matchedEdges, getVertex(vertexList, 'x', e.xId));
            }
        }
        return foundEdges;
    }


    public static void main(String[] args) {
        if(args.length != 1){
            System.out.println("java KM <inputfile.txt>");
            return;
        }

        long startTime = System.nanoTime();
        //One Time Functions:
        LinkedList<Vertex> vertexList = loadInput(args[0]);         //List of all vertices
        LinkedList<Edge> edgeList = loadEdges(vertexList);          //List of all edges
        initializeXlabels(vertexList);                              //Initialize x vertex labels to weight of max edge


        //Functions that will be called multiple times:
        LinkedList<Edge> equalityGraph = findTightEdges(vertexList); //List of tight edges

        LinkedList<Edge> matchedEdges = new LinkedList<Edge>();     // "M set of matched edges"
        LinkedList<Vertex> S = new LinkedList<Vertex>();            // "S set of x vertices"
        LinkedList<Vertex> T = new LinkedList<Vertex>();            // "T set of y vertices"

        LinkedList<Edge> augPath = new LinkedList<Edge>();          //stores an augmenting path
        int alpha = 0;                                              //alpha for label updates
        final int n = vertexList.size() / 2;                        // "n" (perfect matching)

        //TODO: Create function to find/return augmenting path:  augPath = findAugPath(equalityList, matchedEdges);

        //TODO: Create function to return alpha value:  alpha = findAlpha(vertexList, S, T)


        Vertex x1 = getVertex(vertexList, 'x', 1);
        System.out.println("x1: " + x1);

        Vertex y2 = getVertex(vertexList, 'y', 2);
        System.out.println("y2: " + y2);

        System.out.println("x1.edges: " + x1.edges);
        System.out.println("y2.edges: " + y2.edges);

        System.out.println("equalityGraph:" + equalityGraph);



        while(matchedEdges.size() < n){

            //TODO: Check equalityGraph for an augmenting path, and if found 'flip it' and GOTO WHILE.
            LinkedList<Edge> foundEdges = findAugmentingPath(vertexList, equalityGraph, matchedEdges);
            if(foundEdges != null){
                //TODO: Flip the edges in foundEdges
            }



            //TODO: (1) Select a free X vertex. (vertexList where no edge in matchedEdges has this x)
            //TODO: (2) Add the free X to list S.
            //TODO: (3) If N(S) in T, update vertex labels after calculating 'alpha' value.
            //TODO:     --Reload equalityGraph after labels have been updated.  GOTO WHILE.
            //TODO: If N(S) != T, select a y from N(S) - T.
            //TODO:     --If the y is free, add the new (x,y) edge to matchedEdges and GOTO WHILE.
            //TODO:     --If y is already matched to z, add z to S and y to T GOTO 3.

            break;
        }






        long endTime = System.nanoTime();
        long totalTime = endTime - startTime;
        System.out.println("Total time taken for KM is " + totalTime);
        return;
    }
}
