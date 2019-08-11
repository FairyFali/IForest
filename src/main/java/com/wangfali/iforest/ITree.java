package com.wangfali.iforest;

import java.util.Random;

public class ITree {
    public int spiltAttr;
    public double spiltValue;
    public ITree ltree, rtree;
    public int currHeight;
    public int leafNodes;

    public ITree(int attr, double val){
        spiltAttr = attr;
        spiltValue = val;
        ltree = rtree = null;
        currHeight = 0;
        leafNodes = 1;
    }

    @Override
    public String toString() {
        return "spiltAttr=" + spiltAttr + ", spiltValue=" + spiltValue + " , currentHeight=" + currHeight + ", leafNodes=" + leafNodes;
    }

    public static void displayITree(ITree iTree, int tab){
        if(iTree == null) return;
        for(int i=0;i<tab;i++) System.out.print(' ');
        System.out.println(iTree);
        displayITree(iTree.ltree, tab + 4);
        displayITree(iTree.rtree, tab + 4);
    }


    /**
     * 创建ITree
     * @param samples
     * @param currHeight
     * @param limitHeight
     * @return
     */
    public static ITree createITree(double[][] samples, int currHeight, int limitHeight){
        /*for (int i=0;i<samples.length;i++){
            for (int j=0;j<samples[i].length;j++) {
                System.out.print(samples[i][j] + " ");
            }
            System.out.println();
        }*/

        ITree iTree = null;
        if (samples.length == 0) return iTree;
        if (samples.length <= 1 || currHeight >= limitHeight){
            iTree = new ITree(0, samples[0][0]);
            iTree.leafNodes = samples.length;
            iTree.currHeight = currHeight;
            return iTree;
        }

        int rows = samples.length; // 样本数量
        int cols = samples[0].length; // 属性数量

        // 判断样本是否完全相同
        boolean flag = true;
        for (int i=1;i<rows;i++) {
            for(int j=0;j<cols;j++){
                if (samples[i][j] != samples[i-1][j]){
                    flag = false;
                    break;
                }
            }
            if(!flag)break;
        }
        if(flag){
            iTree = new ITree(0, samples[0][0]);
            iTree.leafNodes = samples.length;
            iTree.currHeight = currHeight;
            return iTree;
        }

        // 生成随机属性和随机属性分割值
        Random random = new Random(System.currentTimeMillis());
        int spiltAttr = random.nextInt(cols);
        // 获取随机属性的最大值和最小值
        double maxVal = samples[spiltAttr][0];
        double minVal = samples[spiltAttr][0];
        for (int i=0;i<rows;i++) {
            if(samples[i][spiltAttr] > maxVal){
                maxVal = samples[i][spiltAttr];
            }
            if(samples[i][spiltAttr] < minVal){
                minVal = samples[i][spiltAttr];
            }
        }
        // 随机分割值
        double spiltVal = (maxVal-minVal)*random.nextDouble() + minVal;
        // 获得分割值左子树和右子树的数量
        int leftNum = 0;
        int rightNum = 0;
        for (int i=0;i<rows;i++){
            if (samples[i][spiltAttr] < spiltVal) {
                leftNum ++;
            } else {
                rightNum ++;
            }
        }
        // 获得左侧样本和右侧样本
        double[][] leftSamples = new double[leftNum][cols];
        double[][] rightSamples = new double[rightNum][cols];
        int l=0,r=0;
        for(int i=0;i<rows;i++){
            if (samples[i][spiltAttr] < spiltVal) {
                leftSamples[l++] = samples[i];
                //System.out.println("l:" + samples[i][0] + " " + samples[i][1]);
            } else {
                rightSamples[r++] = samples[i];
                //System.out.println("r:" + samples[i][0] + " " + samples[i][1]);
            }
        }
        // 递归创建左子树和右子树
        ITree parent = new ITree(spiltAttr, spiltVal);
        parent.currHeight = currHeight;
        parent.leafNodes = samples.length;
        parent.ltree = createITree(leftSamples, currHeight + 1, limitHeight);
        parent.rtree = createITree(rightSamples, currHeight + 1, limitHeight);

        return parent;
    }

    /**
     * 获取sample样本的长度
     * @param sample
     */
    public static double pathLength(double[] sample, ITree iTree) throws Exception {
        // 参数合法性检查
        if (sample == null || sample.length == 0) {
            throw new Exception("Sample is null or empty, please check...");
        } else if (iTree == null || iTree.leafNodes == 0) {
            throw new Exception("iTree is null or empty, please check...");
        }
        int cols = sample.length;
        int pathLen = 0;
        int leafNodes = iTree.leafNodes;
        while(iTree != null){
            leafNodes = iTree.leafNodes;
            if(iTree.ltree == null || iTree.rtree == null) break;
            if (sample[iTree.spiltAttr] < iTree.spiltValue){
                pathLen ++;
                iTree = iTree.ltree;
            } else {
                pathLen ++;
                iTree = iTree.rtree;
            }

        }
        return pathLen + c(leafNodes);
    }

    /**
     * 计算c值
     * @return
     */
    public static double c(double n){
        if(n<=1) return 0;
        return 2*(Math.log(n-1) + 0.5772156649) - 2*(n-1)/n;
    }
}
