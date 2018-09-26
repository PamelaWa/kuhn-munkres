/*****************************
 * Social Computing - Fall 2018
 * Prof Garg
 * Homework 1 - Problem 4
 * Kuhn-Munkres Bipartite Matching Algorithm
 * Patrick Sigourney
 * Robert Pate
 *****************************/

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

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
                return (this.partite == vObj.partite && this.id == vObj.id);
            }
            return false;
        }

        @Override
        public String toString(){
            return partite + Integer.toString(id);
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
                return (this.xId == eObj.xId && this.yId == eObj.yId);
            }
            return false;
        }

        @Override
        public String toString(){
            return "(" + xId + "," + yId + ")";
        }
    }

    private static LinkedList<Vertex> loadInput(String inputFile){
        LinkedList<Vertex> vertexList = new LinkedList<Vertex>();
        //Create the X and Y vertices and populated the X edges
        try{
            BufferedReader br = new BufferedReader(new FileReader(inputFile));
            int n = Integer.valueOf(br.readLine());
            for(int i = 1; i <= n; i++){ //For each row - Create Vertices
                Vertex xv = new Vertex('x', i);
                Vertex yv = new Vertex('y', i);

                String line = br.readLine();
                String[] arrLine = (line.split("\\s+"));
                for(int j = 1; j <= n; j++){    //For each column - Create Edges
                    Edge e = new Edge(i, j, Integer.valueOf(arrLine[j-1]));
                    xv.edges.push(e);
                }
                vertexList.push(xv);
                vertexList.push(yv);
            }
        }catch (Exception e) {System.out.println("LoadInput() Exception!: " + e);}

        //Populate edges for the Y vertices
        for(Vertex xv : vertexList){
            if(xv.partite != 'x') continue;
            for(Edge e : xv.edges){
                Vertex y = getVertex(vertexList, 'y', e.yId);
                y.edges.add(e);
            }
        }
        return vertexList;
    }

    private static void initializeXlabels(LinkedList<Vertex> vertexList){
        for(Vertex v :  vertexList){
            if(v.partite == 'y') continue;  //X nodes only
            int maxWeight = 0;
            for(Edge e : v.edges){
                if(e.weight > maxWeight) maxWeight = e.weight;
            }
            v.label = maxWeight;
        }
    }

    private static LinkedList<Edge> findTightEdges(LinkedList<Vertex> vertexList){
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

    private static Vertex getVertex(LinkedList<Vertex> vertexList, char partiteParam, int idParam){
        for(Vertex v : vertexList){
            if(v.partite == partiteParam && v.id == idParam){
                return v;
            }
        }
        return null;
    }

    private static Edge getEdge(LinkedList<Edge> edgeList, int xParam, int yParam){
        for(Edge e : edgeList){
            if(e.xId == xParam && e.yId == yParam)
                return e;
        }
        return null;
    }

    private static LinkedList<Vertex> getEqualityVertices(LinkedList<Vertex> vertexList, LinkedList<Edge> tightEdges){
        LinkedList<Vertex> equalityVertices = new LinkedList<Vertex>();
        for(Edge e : tightEdges){
            Vertex xVertex = getVertex(vertexList, 'x', e.xId);
            Vertex yVertex = getVertex(vertexList, 'y', e.yId);

            if(!equalityVertices.contains(xVertex))
                equalityVertices.add(getVertex(vertexList, 'x', e.xId));
            if(!equalityVertices.contains(yVertex))
                equalityVertices.add(getVertex(vertexList, 'y', e.yId));
        }
        return equalityVertices;
    }

    private static LinkedList<Edge> findAugmentingPath(LinkedList<Vertex> vertexList, LinkedList<Edge> tightEdges, LinkedList<Edge> matchedEdges){
        LinkedList<Edge> augmentedPath = new LinkedList<Edge>();
        LinkedList<Vertex> equalityVertices = getEqualityVertices(vertexList, tightEdges);

       for(Vertex v : equalityVertices) {
           augmentedPath.clear();
           augmentedPath = checkForUnmatchedEdge(augmentedPath, vertexList, tightEdges, matchedEdges, v);
           if (augmentedPath.size() >= 3) {
               return augmentedPath;
           }
       }
       return null;
    }

    private static LinkedList<Edge> checkForUnmatchedEdge(LinkedList<Edge> augmentedPath, LinkedList<Vertex> vertexList, LinkedList<Edge> tightEdges, LinkedList<Edge> matchedEdges, Vertex toCheck){
        for(Edge e : toCheck.edges){
            //Ensure edge is tight and unmatched
            if (!tightEdges.contains(e)) continue;
            if (matchedEdges.contains(e)) continue;

            //Get the vertex for the next hop
            char nextPartite;
            int nextId;
            if(toCheck.partite == 'x'){
                nextPartite = 'y';
                nextId = e.yId;
            }
            else {
                nextPartite = 'x';
                nextId = e.xId;
            }
            Vertex nextVertex = getVertex(vertexList, nextPartite, nextId);
            if(nextVertex == null) return augmentedPath;

            if(!augmentedPath.contains(e)) augmentedPath.add(e);
            return checkForMatchedEdge(augmentedPath, vertexList, tightEdges, matchedEdges, nextVertex);
        }
        return augmentedPath;
    }


    private static LinkedList<Edge> checkForMatchedEdge(LinkedList<Edge> augmentedPath, LinkedList<Vertex> vertexList, LinkedList<Edge> tightEdges, LinkedList<Edge> matchedEdges, Vertex toCheck){
        for(Edge e : toCheck.edges){
            //Ensure edge is tight and matched
            if (!tightEdges.contains(e)) continue;
            if (!matchedEdges.contains(e)) continue;

            //Get the vertex for the next hop
            char nextPartite;
            int nextId;
            if(toCheck.partite == 'x'){
                nextPartite = 'y';
                nextId = e.yId;
            }
            else {
                nextPartite = 'x';
                nextId = e.xId;
            }
            Vertex nextVertex = getVertex(vertexList, nextPartite, nextId);
            if(nextVertex == null) return augmentedPath;

            if(!augmentedPath.contains(e)) augmentedPath.add(e);
            return checkForUnmatchedEdge(augmentedPath, vertexList, tightEdges, matchedEdges, nextVertex);
        }
        return augmentedPath;
    }



    private static void flipAugmentingPath(LinkedList<Edge> foundEdges, LinkedList<Edge> matchedEdges){
        //Take the list of foundEdges, if present in matchedEdges, remove them.
        //If not present in matchedEdges, add them.

        LinkedList<Edge> toAdd = new LinkedList<Edge>();
        LinkedList<Edge> toDel = new LinkedList<Edge>();

        for(Edge e : foundEdges){
            if(matchedEdges.contains(e)) toDel.add(e);
            else toAdd.add(e);
        }

        matchedEdges.removeAll(toDel);
        matchedEdges.addAll(toAdd);
    }

    private static Vertex findUnmatchedVertex(LinkedList<Vertex> vertexList, LinkedList<Edge> matchedEdges){
        //For x vertex in vertexList, find the first vertex which doesn't have a matched edge attached.
        for(Vertex v : vertexList){
            if(v.partite == 'y') continue; //Ignore the y vertices
            boolean matched = false;
            for(Edge e : matchedEdges){
                if(e.xId == v.id) matched = true;
            }
            if(!matched) return v;
        }
        return null;
    }

    private static LinkedList<Vertex> findNeighborsOfS(LinkedList<Vertex> vertexList, LinkedList<Edge> tightEdges, LinkedList<Vertex> S){
        LinkedList<Vertex> neighbors = new LinkedList<Vertex>();
        for (Vertex v : S){
            for (Edge e : v.edges){
                if(!tightEdges.contains(e)) continue;
                Vertex yNeighbor = getVertex(vertexList, 'y', e.yId);
                if(yNeighbor != null && !neighbors.contains(yNeighbor))
                    neighbors.add(yNeighbor);
            }
        }
        return neighbors;
    }


    private static Vertex selectYFromNsMinusT(LinkedList<Vertex> Ns, LinkedList<Vertex> T){
        for(Vertex neighbor : Ns){
            if(!T.contains(neighbor)) return neighbor;
        }
        return null;
    }

    private static Edge findYsMatchingEdge(Vertex y, LinkedList<Edge> matchedEdges){
        for(Edge e : matchedEdges){
            if (e.yId == y.id) return e;
        }
        return null;
    }

    private static Integer findAlpha(LinkedList<Vertex> vertexList, LinkedList<Vertex> S, LinkedList<Vertex> T){
        //Alpha is the minimum slack, where x in S, y not in T.
        // min( l(x) + l(y) - w(x,y) )
        int minWeight = -1;
        Map<Edge, Integer> results = new HashMap<Edge, Integer>();
        for(Vertex x : S){
            for(Edge e : x.edges){
                Vertex y = getVertex(vertexList, 'y', e.yId);
                if(T.contains(y)) continue; //Don't want this one.
                Integer slack = x.label + y.label - e.weight;
                results.put(e, slack);
                minWeight = slack;
            }
        }

        for(Edge e : results.keySet()){
            if(results.get(e) <= minWeight) minWeight = results.get(e);
        }
        return minWeight;
    }

    private static void updateVertexLabels(LinkedList<Vertex> vertexList, Integer alpha, LinkedList<Vertex> S, LinkedList<Vertex> T){
        for(Vertex v : vertexList){
            if(S.contains(v)) v.label -= alpha;
            if(T.contains(v)) v.label += alpha;
        }
    }

    private static boolean sameElements(LinkedList<Vertex> a, LinkedList<Vertex> b){
        if(a == null || b == null) return false;
        if(a.size() != b.size()) return false;
        for(Vertex v : a){
            if(!b.contains(v)) return false;
        }
        for(Vertex v : b){
            if(!a.contains(v)) return false;
        }
        return true;
    }



    public static void main(String[] args) {
        if(args.length != 1){
            System.out.println("java KM <inputfile.txt>");
            return;
        }

        LinkedList<Vertex> S = new LinkedList<Vertex>();            // "S set of x vertices"
        LinkedList<Vertex> Ns;                                      // "N(s) neighbors of S"
        LinkedList<Vertex> T = new LinkedList<Vertex>();            // "T set of y vertices"
        LinkedList<Edge> matchedEdges = new LinkedList<Edge>();     // "M set of matched edges"

        LinkedList<Vertex> vertexList = loadInput(args[0]);         //List of all vertices
        initializeXlabels(vertexList);                              //Initialize x vertex labels to weight of max edge
        final int n = vertexList.size() / 2;                        // "n" (perfect matching)
        int alpha;                                              //alpha for label updates

        long startTime = System.nanoTime();

        while(matchedEdges.size() < n){
            LinkedList<Edge> tightEdges = findTightEdges(vertexList);
            LinkedList<Vertex> equalityVertices = getEqualityVertices(vertexList, tightEdges);

            //STEP 2
            //
            // Check matchedEdges for an augmenting path and flip if found:
            LinkedList<Edge> foundEdges = findAugmentingPath(vertexList, tightEdges, matchedEdges);
            if(foundEdges != null){
                flipAugmentingPath(foundEdges, matchedEdges);
                continue; //Restart the while loop
            }

            // Otherwise pick a free x vertex and add it to S.
            Vertex u = findUnmatchedVertex(equalityVertices, matchedEdges);

            S.clear();
            S.add(u);
            T.clear();
            Ns = findNeighborsOfS(equalityVertices, tightEdges, S);

            while(true) {
                //STEP 3
                //
                // If N(s) = T, find alpha and update vertex labels
                if (sameElements(Ns, T)) {
                    alpha = findAlpha(vertexList, S, T);
                    updateVertexLabels(vertexList, alpha, S, T);
                    break; // break the while(true) loop, goto Step 2
                }
                //STEP 4
                //
                // If N(s) != T, pick a y from N(s)-T
                else{
                    Vertex y = selectYFromNsMinusT(Ns, T);
                    if (findYsMatchingEdge(y, matchedEdges) == null) {        //If y is free
                        Edge newMatchedEdge = getEdge(tightEdges, u.id, y.id);
                        if(newMatchedEdge != null){
                            matchedEdges.add(newMatchedEdge);
                        }
                        break; // break the while(true) loop; Go to Step 2
                    } else {                                                //y is not free, it is matched to z
                        Edge e = findYsMatchingEdge(y, matchedEdges);
                        Vertex z = getVertex(vertexList, 'x', e.xId);
                        S.add(z);
                        Ns = findNeighborsOfS(vertexList, tightEdges, S);
                        T.add(y);
                        //Go to Step 3
                    }
                }
            }
        }

        long endTime = System.nanoTime();
        long totalTime = endTime - startTime;
        System.out.println("Total time taken for KM is " + totalTime);

        int totalWeight = 0;
        for(Edge e : matchedEdges)totalWeight += e.weight;
        System.out.println(totalWeight);
        matchedEdges.sort(new Comparator<Edge>() {
            @Override
            public int compare(Edge o1, Edge o2) {
                return o1.xId - o2.xId;
            }
        });
        for(Edge e : matchedEdges)System.out.println(e);
    }
}
