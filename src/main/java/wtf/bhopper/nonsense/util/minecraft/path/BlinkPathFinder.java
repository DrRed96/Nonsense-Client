package wtf.bhopper.nonsense.util.minecraft.path;

import net.minecraft.util.BlockPos;
import net.minecraft.util.Vec3;
import org.jetbrains.annotations.NotNull;
import wtf.bhopper.nonsense.util.minecraft.world.BlockUtil;
import wtf.bhopper.nonsense.util.misc.MathUtil;

import java.util.ArrayList;
import java.util.List;

public class BlinkPathFinder {

    private static final Vec3[] DIRECTIONS = {
            new Vec3(1, 0, 0),
            new Vec3(-1, 0, 0),
            new Vec3(0, 0, 1),
            new Vec3(0, 0, -1),
            new Vec3(0, 1, 0),
            new Vec3(0, -1, 0)
    };

    private final Vec3 start;
    private final Vec3 goal;
    private final List<Vec3> path = new ArrayList<>();
    private final List<Node> nodes = new ArrayList<>();
    private final List<Node> workingNodes = new ArrayList<>();
    private final double minDistanceSq;

    /**
     * Construct a new blink pathfinder
     * @param start the starting position
     * @param goal the goal (ending position)
     * @param minDistance minimum inaccuracy distance, set this to 0 to force the patch finder to be exact
     */
    public BlinkPathFinder(Vec3 start, Vec3 goal, double minDistance) {
        this.start = start;
        this.goal = goal;
        this.minDistanceSq = minDistance * minDistance;
    }
    
    public List<Vec3> getPath() {
        return this.path;
    }

    /**
     * Computes the path
     */
    public void compute() {
        this.compute(1000, 4);
    }

    /**
     * Compute the path
     * @param maxLoops maximum amount of search loops the pathfinder is allowed to compute.
     * @param maxDepth maximum amount of nodes the pathfinder is allowed to search each loop.
     */
    public void compute(int maxLoops, int maxDepth) {
        this.path.clear();
        this.workingNodes.clear();
        this.workingNodes.add(new Node(null, this.start, new ArrayList<>(List.of(this.start)), this.start.squareDistanceTo(this.goal), 0.0, 0.0));

        search:
        for (int i = 0; i < maxLoops; i++) {
            this.workingNodes.sort(Node::compareTo);

            if (this.workingNodes.isEmpty()) {
                break;
            }

            int depth = 0;
            for (Node node : new ArrayList<>(this.workingNodes)) {
                depth++;
                if (depth > maxDepth) {
                    break;
                }

                this.workingNodes.remove(node);
                this.nodes.add(node);

                for (Vec3 direction : DIRECTIONS) {
                    Vec3 pos = MathUtil.floor(node.getPos().add(direction));
                    if (this.isPositionValid(new BlockPos(pos))) {
                        if (this.addNode(node, pos, 0.0)) {
                            break search;
                        }
                    }
                }
            }
        }


        this.nodes.sort(Node::compareTo);
        this.path.clear();
        this.path.addAll(this.nodes.getFirst().getPath());
    }

    private boolean addNode(Node parent, Vec3 pos, double cost) {
        Node existingNode = this.getExistingNode(pos);
        double totalCost = cost;
        if (parent != null) {
            totalCost += parent.getTotalCost();
        }
        if (existingNode == null) {
            if (MathUtil.vecEqual(pos, this.goal) || (this.minDistanceSq != 0 && pos.squareDistanceTo(this.goal) <= minDistanceSq)) {
                this.path.clear();
                if (parent != null) {
                    this.path.addAll(parent.getPath());
                }
                this.path.add(pos);
                return true;
            }

            List<Vec3> newPath = parent != null ? new ArrayList<>(parent.getPath()) : new ArrayList<>();
            newPath.add(pos);
            this.workingNodes.add(new Node(parent, pos, newPath, pos.squareDistanceTo(this.goal), cost, totalCost));

        } else if (existingNode.getCost() > cost) {
            List<Vec3> newPath = parent != null ? new ArrayList<>(parent.getPath()) : new ArrayList<>();
            newPath.add(pos);
            existingNode.setParent(parent);
            existingNode.setPos(pos);
            existingNode.setPath(newPath);
            existingNode.setDistanceSq(pos.squareDistanceTo(this.goal));
            existingNode.setCost(cost);
            existingNode.setCost(totalCost);
        }

        return false;
    }

    private Node getExistingNode(Vec3 pos) {
        for (Node node : this.nodes) {
            if (MathUtil.vecEqual(node.getPos(), pos)) {
                return node;
            }
        }
        for (Node node : this.workingNodes) {
            if (MathUtil.vecEqual(node.getPos(), pos)) {
                return node;
            }
        }
        return null;
    }

    private boolean isPositionValid(BlockPos pos) {
        BlockPos up = pos.up();
        BlockPos down = pos.down();
        return !BlockUtil.isSolid(pos) && !BlockUtil.isSolid(up) && BlockUtil.isSafeToWalkOn(down);
    }

    private static class Node implements Comparable<Node> {
        private Node parent;
        private Vec3 pos;
        private List<Vec3> path;
        private double distanceSq;  // g score
        private double cost;        // f score
        private double totalCost;

        public Node(Node parent, Vec3 pos, List<Vec3> path, double distanceSq, double cost, double totalCost) {
            this.parent = parent;
            this.pos = pos;
            this.path = path;
            this.distanceSq = distanceSq;
            this.cost = cost;
            this.totalCost = totalCost;
        }

        public Node getParent() {
            return this.parent;
        }

        public void setParent(Node parent) {
            this.parent = parent;
        }

        public Vec3 getPos() {
            return this.pos;
        }

        public void setPos(Vec3 pos) {
            this.pos = pos;
        }

        public List<Vec3> getPath() {
            return this.path;
        }

        public void setPath(List<Vec3> path) {
            this.path = path;
        }

        public double getDistanceSq() {
            return this.distanceSq;
        }

        public void setDistanceSq(double distanceSq) {
            this.distanceSq = distanceSq;
        }

        public double getCost() {
            return this.cost;
        }

        public void setCost(double cost) {
            this.cost = cost;
        }

        public double getTotalCost() {
            return this.totalCost;
        }

        public void setTotalCost(double totalCost) {
            this.totalCost = totalCost;
        }

        @Override
        public int compareTo(@NotNull BlinkPathFinder.Node o) {
            return (int)((this.distanceSq + this.totalCost) - (o.distanceSq + o.totalCost));
        }
    }

}
