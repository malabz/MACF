package hierCluster;

import java.util.ArrayList;
import java.util.List;

public class upgma {
    private double[][] dmatrix;
    public List<int[]> TreeList;
    private node[] nodes;
    private int[] nums;
    private int n, global_n;
    private boolean[] state;

    public upgma(double[][] matrix) {
        this.dmatrix = matrix;
        this.n = matrix.length;
        this.nums = new int[this.n];
        this.nodes = new node[this.n];
        this.state = new boolean[this.n];
        for (int i = 0; i < n; i++) {
            this.nums[i] = 1;
            node temp = new leafnode("" + i, i);
            this.nodes[i] = temp;
            state[i] = true;
        }
        this.global_n = this.n;
        this.TreeList = new ArrayList<>();
    }

    // for clusterTree
    public upgma(double[][] matrix, int[] idxs, int globaln) {
        this.dmatrix = matrix;
        this.n = matrix.length;
        this.nums = new int[this.n];
        this.nodes = new node[this.n];
        this.state = new boolean[this.n];
        for (int i = 0; i < n; i++) {
            this.nums[i] = 1;
            node temp = new leafnode("" + idxs[i], idxs[i]);
            this.nodes[i] = temp;
            state[i] = true;
        }
        this.global_n = globaln;
        this.TreeList = new ArrayList<>();
    }

    /**
     * find the minimum value and its idx idxj > idxi
     */
    private int[] findMinimum() {
        int idxi = 0;
        int idxj = 1;
        double minimum = this.dmatrix[idxi][idxj];
        for (int i = 0; i < dmatrix.length; i++) {
            if (!state[i])
                continue;
            for (int j = i + 1; j < dmatrix.length; j++) {
                if (!state[j])
                    continue;
                if (this.dmatrix[i][j] > minimum) {
                    idxi = i;
                    idxj = j;
                    minimum = dmatrix[idxi][idxj];
                }
            }
        }
        return new int[] { idxi, idxj };
    }

    /**
     * combine the two nodes
     */
    private void combineNodes(int idxi, int idxj) {
        double minimum = 1 - this.dmatrix[idxi][idxj];
        node newnode = new midnode(this.nodes[idxi], this.nodes[idxj], this.global_n++);
        this.nodes[idxi].setLen(minimum / 2 - this.nodes[idxi].getDistance());
        this.nodes[idxj].setLen(minimum / 2 - this.nodes[idxj].getDistance());
        int[] treelist = { this.nodes[idxi].getNum(), this.nodes[idxj].getNum(), this.global_n - 1 };
        this.TreeList.add(treelist.clone());
        this.nodes[idxi] = newnode;
        this.nodes[idxj] = new leafnode("pass", -1);
    }

    /**
     * renew upright area of the distance matrix
     */
    private void renewDist(int idxi, int idxj) {
        for (int i = 0; i < idxi; i++) {
            if (!state[i])
                continue;
            dmatrix[i][idxi] = (dmatrix[i][idxi] * nums[idxi] + dmatrix[i][idxj] * nums[idxj])
                    / (nums[idxi] + nums[idxj]);
        }
        for (int j = idxi + 1; j < this.n; j++) {
            if (!state[j])
                continue;
            if (j < idxj)
                dmatrix[idxj][j] = dmatrix[j][idxj];
            dmatrix[idxi][j] = (dmatrix[idxi][j] * nums[idxi] + dmatrix[idxj][j] * nums[idxj])
                    / (nums[idxi] + nums[idxj]);
        }
        for (int i = 0; i < idxj; i++)
            dmatrix[i][idxj] = Double.NEGATIVE_INFINITY;
        for (int j = idxj + 1; j < this.n; j++)
            dmatrix[idxj][j] = Double.NEGATIVE_INFINITY;
    }

    /**
     * renew nums
     */
    private void renewNum(int idxi, int idxj) {
        // renew n
        this.n--;
        this.nums[idxi] += nums[idxj];
        this.nums[idxj] = 0;
        state[idxj] = false;
    }

    public void genTree() {
        if (this.n < 2) {
            throw new IllegalArgumentException("The number of strings is smaller than 2!");
        }
        while (this.n > 2) {
            // find the minimum value and its idx
            int[] idxij = findMinimum();
            // combine the two nodes
            combineNodes(idxij[0], idxij[1]);
            // renew upright area of the distance matrix
            renewDist(idxij[0], idxij[1]);
            // renew nums
            renewNum(idxij[0], idxij[1]);
        }
        int idxi = -1, idxj = -1;
        for (int i = 0; i < state.length; i++) {
            if (state[i]) {
                if (idxi == -1)
                    idxi = i;
                else {
                    idxj = i;
                    break;
                }
            }
        }
        new midnode(this.nodes[idxi], this.nodes[idxj], this.global_n);
        double len = (1 - this.dmatrix[idxi][idxj]) / 2;
        this.nodes[idxi].setLen(len - this.nodes[idxi].getDistance());
        this.nodes[idxj].setLen(len - this.nodes[idxj].getDistance());
        int[] treelist = { this.nodes[idxi].getNum(), this.nodes[idxj].getNum(), this.global_n };
        this.TreeList.add(treelist.clone());
    }
}