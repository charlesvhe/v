package charles.v.math;

import org.apache.commons.math3.optim.MaxIter;
import org.apache.commons.math3.optim.PointValuePair;
import org.apache.commons.math3.optim.linear.*;
import org.apache.commons.math3.optim.nonlinear.scalar.GoalType;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

public class TestLinear {
    @Test
    public void testLinear() {
/*
This would maximize the following linear problem:
f = 2*x2 + 2*x1 + x0
with
x2 + x1 <= 5
x2 + x0 <= 4
x1 <= 3
*/
        LinearObjectiveFunction function = new LinearObjectiveFunction(new double[]{2, 2, 1}, 0);
        Collection<LinearConstraint> constraints = new ArrayList<LinearConstraint>();
        constraints.add(new LinearConstraint(new double[]{1, 1, 0}, Relationship.LEQ, 5));
        constraints.add(new LinearConstraint(new double[]{1, 0, 1}, Relationship.LEQ, 4));
        constraints.add(new LinearConstraint(new double[]{0, 1, 0}, Relationship.LEQ, 3));

        SimplexSolver solver = new SimplexSolver();
        PointValuePair solution = solver.optimize(new MaxIter(100), function, new LinearConstraintSet(constraints), GoalType.MAXIMIZE, new NonNegativeConstraint(true));
        System.out.println(Arrays.toString(solution.getPoint()));
    }
}
