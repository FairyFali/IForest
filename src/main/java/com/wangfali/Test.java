package com.wangfali;

import com.wangfali.iforest.IForest;

public class Test {
    public static void main(String[] args) throws Exception {
        /*double[][] samples = {{1, 2}, {1.1, 2}, {1, 2.1}, {1.1, 2.1}, {0.1, 0.1}};
        ITree iTree = ITree.createITree(samples, 0, 3);

        ITree.displayITree(iTree, 0);
        System.out.println(ITree.pathLength(new double[]{1, 1}, iTree));*/

        double[][] samples = {{1, 2}, {1.1, 2}, {1, 2.1}, {1.1, 2.1}, {0.1, 0.1}};
        IForest iForest= IForest.createIForest(samples, 3, 3);
        //ITree.displayITree(iForest.subTrees.get(0), 0);
        double[] scores = iForest.score(iForest);
//        for(int i=0;i<scores.length;i++) System.out.print(scores[i] + " ");
//        System.out.println();
        int[] labels = iForest.sampleLabel(scores, 10);
        // 0说明是异常点，1说明是正常点
        for(int i=0;i<labels.length;i++) System.out.print(labels[i] + " ");
        System.out.println();
        // 预测新的点是否是异常点
        System.out.println(iForest.predict(new double[]{2,2}));
    }
}
