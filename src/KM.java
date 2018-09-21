

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.LinkedList;


public class KM {

    static class Vertex{
        public char partite;
        public Integer id;
        public LinkedList<Edge> edges = new LinkedList<Edge>();

        public Vertex(char partiteParam, int idParam){
            partite = partiteParam;
            id = idParam;
        }

        public boolean equals(Object obj){
            if(this == obj)
                return true;
            if(obj == null)
                return false;
            if(obj instanceof Vertex){
                Vertex vObj = (Vertex)obj;
                if(this.partite == vObj.partite && this.id == vObj.id)
                    return true;
            }
            return false;

        }
    }

    static class Edge{
        public int x;
        public int y;
        public int weight = 0;

        public Edge(int xParam, int yParam, int weightParam){
            x = xParam;
            y = yParam;
            weight = weightParam;
        }

        public boolean equals(Object obj){
            if(this == obj)
                return true;
            if(obj == null)
                return false;
            if(obj instanceof Edge){
                Edge eObj = (Edge)obj;
                if(this.x == eObj.x && this.y == eObj.y)
                    return true;
            }
            return false;

        }


    }

    public static LinkedList<Vertex> LoadInput(String inputFile){
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
                    Edge e = new Edge(i, j, Integer.valueOf(arrLine[0]));
                    xv.edges.push(e);
                    yv.edges.push(e);
                }
                vertexList.push(xv);
                vertexList.push(yv);

            }
        }catch (Exception e) {System.out.println("LoadInput() Exception!: " + e);}
        return vertexList;

    }

    public static Vertex getVertex(LinkedList<Vertex> vertexList, char partiteParam, int idParam){
        for(Vertex v : vertexList){
            if(v.partite == partiteParam && v.id == idParam)
                return v;
        }
        return null;
    }


    public static void main(String[] args) {
        if(args.length != 1){
            System.out.println("java KM <inputfile.txt>");
            return;
        }

        long startTime = System.nanoTime();
        LinkedList<Vertex> vertexList = LoadInput(args[0]);
        LinkedList<Edge> matchedEdges = new LinkedList<Edge>();
        final int n = vertexList.size() / 2;
        while(matchedEdges.size() < n){
            //TODO: Do the algorithm
        }






        long endTime = System.nanoTime();
        long totalTime = endTime - startTime;
        System.out.println("Total time taken for KM is " + totalTime);
        return;
    }
}
