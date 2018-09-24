

import java.io.BufferedReader;
import java.io.FileReader;
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
                if(this.partite == vObj.partite && this.id == vObj.id)
                    return true;
            }
            return false;
        }

        @Override
        public String toString(){
            //return partite + Integer.toString(id) + ":W" + Integer.toString(label);
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
                if(this.xId == eObj.xId && this.yId == eObj.yId)
                    return true;
            }
            return false;
        }

        @Override
        public String toString(){
            //return "(" + xId + "," + yId + "):W" + weight;
            return "(" + xId + "," + yId + ")";
        }

    }

    public static LinkedList<Vertex> loadInput(String inputFile){
        LinkedList<Vertex> vertexList = new LinkedList<Vertex>();
        //Create the X and Y vertices and populated the X edges
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



    public static Vertex getVertex(LinkedList<Vertex> vertexList, char partiteParam, int idParam){
        //System.out.print("getVertex(): " + partiteParam + idParam + "; vertexList.size(): " + vertexList.size());
        for(Vertex v : vertexList){
            //System.out.print("; checking: " + v.partite + v.id.toString());
            if(v.partite == partiteParam && v.id == Integer.valueOf(idParam)){
                //System.out.println(" FOUND");
                return v;
            }

        }
        //System.out.println("getVertex didn't find a match!!!!");
        return null;
    }

    public static Edge getEdge(LinkedList<Edge> edgeList, int xParam, int yParam){
        for(Edge e : edgeList){
            if(e.xId == xParam && e.yId == yParam)
                return e;
        }
        return null;
    }

    public static LinkedList<Vertex> getEqualityVertices(LinkedList<Vertex> vertexList, LinkedList<Edge> tightEdges){
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

    public static LinkedList<Edge> findAugmentingPath(LinkedList<Vertex> vertexList, LinkedList<Edge> tightEdges, LinkedList<Edge> matchedEdges){
        LinkedList<Edge> augmentedPath = new LinkedList<Edge>();
        LinkedList<Vertex> checkedVertices = new LinkedList<Vertex>();
        LinkedList<Vertex> equalityVertices = getEqualityVertices(vertexList, tightEdges);

        System.out.println("\n*** findAugmentingPath.tightEdges: " + tightEdges);
        System.out.println("*** findAugmentingPath.equalityVertices: " + equalityVertices);
        System.out.println("*** findAugmentingPath.matchedEdges: " + matchedEdges);

       for(Vertex v : equalityVertices) {
           System.out.println("findAugmentingPath for VERTEX: " + v);
           augmentedPath.clear();
           augmentedPath = checkForUnmatchedEdge(augmentedPath, checkedVertices, vertexList, tightEdges, matchedEdges, v);
           System.out.println("findAugmentingPath().augmentedPath RETURNED: " + augmentedPath + "\n");
           if (augmentedPath.size() >= 3) {
               return augmentedPath;
           }
       }
        System.out.println("findAugmentingPath(): No augmented path found" + "\n");
       return null;
    }

    public static LinkedList<Edge> checkForUnmatchedEdge(LinkedList<Edge> augmentedPath, LinkedList<Vertex> checkedVertices, LinkedList<Vertex> vertexList, LinkedList<Edge> tightEdges, LinkedList<Edge> matchedEdges, Vertex toCheck){
        System.out.println("    checkforUnmatchedEdge() vertex: " + toCheck);
        //if(checkedVertices.contains(toCheck)) return augmentedPath;
        //checkedVertices.add(toCheck);

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
            return checkForMatchedEdge(augmentedPath, checkedVertices, vertexList, tightEdges, matchedEdges, nextVertex);
        }
        return augmentedPath;
    }


    public static LinkedList<Edge> checkForMatchedEdge(LinkedList<Edge> augmentedPath, LinkedList<Vertex> checkedVertices, LinkedList<Vertex> vertexList, LinkedList<Edge> tightEdges, LinkedList<Edge> matchedEdges, Vertex toCheck){
        System.out.println("    checkforMatchedEdge() vertex: " + toCheck);
        System.out.println("    checkforMatchedEdge() augmentedPath.size(): " + augmentedPath.size());
        //if(checkedVertices.contains(toCheck)) return augmentedPath;
        //checkedVertices.add(toCheck);

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
            return checkForUnmatchedEdge(augmentedPath, checkedVertices, vertexList, tightEdges, matchedEdges, nextVertex);
        }
        return augmentedPath;
    }



    public static LinkedList<Edge> flipAugmentingPath(LinkedList<Edge> foundEdges, LinkedList<Edge> matchedEdges){
        //Take the list of foundEdges, if present in matchedEdges, remove them.
        //If not present in matchedEdges, add them.

        LinkedList<Edge> toAdd = new LinkedList<Edge>();
        LinkedList<Edge> toDel = new LinkedList<Edge>();

        for(Edge e : foundEdges){
            if(matchedEdges.contains(e)) toDel.add(e);
            else toAdd.add(e);
        }

        for(Edge e : toDel) matchedEdges.remove(e);
        for(Edge e : toAdd) matchedEdges.add(e);
     return matchedEdges;
    }

    public static Vertex findUnmatchedVertex(LinkedList<Vertex> vertexList, LinkedList<Edge> matchedEdges){
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

    public static LinkedList<Vertex> findNeighborsOfS(LinkedList<Vertex> vertexList, LinkedList<Edge> tightEdges, LinkedList<Vertex> S){
        LinkedList<Vertex> neighbors = new LinkedList<Vertex>();
        System.out.println("findNeighborsofS; s: " + S);
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


    public static Vertex selectYFromNsMinusT(LinkedList<Vertex> Ns, LinkedList<Vertex> T){
        for(Vertex neighbor : Ns){
            if(!T.contains(neighbor)) return neighbor;
        }
        return null;
    }

    public static Edge findYsMatchingEdge(Vertex y, LinkedList<Edge> matchedEdges){
        for(Edge e : matchedEdges){
            if (e.yId == y.id) return e;
        }
        return null;
    }

    public static Integer findAlpha(LinkedList<Vertex> vertexList, LinkedList<Vertex> S, LinkedList<Vertex> T){
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

        assert(results.size() > 0) : " findAlpha() empty results";
        for(Edge e : results.keySet()){
            if(results.get(e) <= minWeight) minWeight = results.get(e);
        }
        assert(minWeight >= 0) : " findAlpha() minWeight is negative";
        return minWeight;

    }

    public static LinkedList<Vertex> updateVertexLabels(LinkedList<Vertex> vertexList, Integer alpha, LinkedList<Vertex> S, LinkedList<Vertex> T){
        for(Vertex v : vertexList){
            if(S.contains(v)) v.label -= alpha;
            if(T.contains(v)) v.label += alpha;
        }
        return vertexList;
    }


    public static boolean sameElements(LinkedList<Vertex> a, LinkedList<Vertex> b){
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

        long startTime = System.nanoTime();

        LinkedList<Vertex> S = new LinkedList<Vertex>();            // "S set of x vertices"
        LinkedList<Vertex> Ns;                                      // "N(s) neighbors of S"
        LinkedList<Vertex> T = new LinkedList<Vertex>();            // "T set of y vertices"
        LinkedList<Edge> matchedEdges = new LinkedList<Edge>();     // "M set of matched edges"

        LinkedList<Vertex> vertexList = loadInput(args[0]);         //List of all vertices
        initializeXlabels(vertexList);                              //Initialize x vertex labels to weight of max edge
        final int n = vertexList.size() / 2;                        // "n" (perfect matching)
        int alpha = 0;                                              //alpha for label updates


        System.out.println("Starting algo; vertexList: " + vertexList);
        for(Vertex v : vertexList){
            System.out.print("Vertex: " + v + " Edges: ");
            for (Edge e : v.edges){
                System.out.print( e + "; ");
            }
            System.out.print("\n");
        }


        System.out.println("n = " + n);

        //int outerCounter = 0;
        while(matchedEdges.size() < n){ //&& outerCounter < 50){
            //outerCounter += 1;
            LinkedList<Edge> tightEdges = findTightEdges(vertexList);
            LinkedList<Vertex> equalityVertices = getEqualityVertices(vertexList, tightEdges);

            //STEP 2
            //
            // Check matchedEdges for an augmenting path and flip if found:
            LinkedList<Edge> foundEdges = findAugmentingPath(vertexList, tightEdges, matchedEdges);
            if(foundEdges != null){
                System.out.println("flipping augmenting path");
                matchedEdges = flipAugmentingPath(foundEdges, matchedEdges);
                continue; //Restart the while loop
            }

            // Otherwise pick a free x vertex and add it to S.
            Vertex u = findUnmatchedVertex(equalityVertices, matchedEdges);
            System.out.println("\nUnmatchedVertex u: " + u);
            assert(u != null) : " No free x vertex found";  //If we didn't find a free x vertex, matchedEdges.size() should == n

            // Make S = {u} and T = 0
            S.clear();
            S.add(u);
            T.clear();
            Ns = findNeighborsOfS(equalityVertices, tightEdges, S);
            System.out.println("1) S: " + S);
            System.out.println("1) Neighbors of S: " + Ns);
            System.out.println("1) T: " + T);

            boolean loop = true;
            while(true && loop) {
                //STEP 3
                //
                // If N(s) = T, find alpha and update vertex labels
                if (sameElements(Ns, T)) {
                    alpha = findAlpha(vertexList, S, T);
                    System.out.println("N(s) = t; Updating labels with alpha: " + alpha);
                    vertexList = updateVertexLabels(vertexList, alpha, S, T);
                    tightEdges = findTightEdges(vertexList);
                    equalityVertices = getEqualityVertices(vertexList, tightEdges);
                    loop = false;

                }
                //STEP 4
                //
                // If N(s) != T, pick a y from N(s)-T
                else{
                    Vertex y = selectYFromNsMinusT(Ns, T);
                    System.out.println("N(s) != T; selecting vertex y: " + y);
                    if (findYsMatchingEdge(y, matchedEdges) == null) {        //If y is free
                        Edge newMatchedEdge = getEdge(tightEdges, u.id, y.id);
                        if(newMatchedEdge != null){
                            System.out.println("y is free, adding to matchedEdges");
                            matchedEdges.add(newMatchedEdge);
                            System.out.println("Matched Edges: " + matchedEdges);

                        }
                        break; // break the while(true) loop; Go to Step 2
                    } else {                                                //y is not free, it is matched to z
                        Edge e = findYsMatchingEdge(y, matchedEdges);
                        Vertex z = getVertex(vertexList, 'x', e.xId);
                        System.out.println("y is not free, matched to " + z);
                        S.add(z);
                        Ns = findNeighborsOfS(vertexList, tightEdges, S);
                        T.add(y);
                        System.out.println("2) S: " + S);
                        System.out.println("2) Neighbors of S: " + Ns);
                        System.out.println("2) T: " + T);
                        continue; //continue the while(true) loop; Go to Step 3
                    }
                }
            }
        }



        long endTime = System.nanoTime();
        long totalTime = endTime - startTime;
        System.out.println("Total time taken for KM is " + totalTime);

        System.out.println("matchedEdges: " + matchedEdges);

        return;
    }
}
