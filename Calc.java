
/**
 * 
 */
public class Calc
{
    public static final double COULOMB_CONSTANT = 8.987_551_785_972E9;
    public static final double VACUUM_PERMEABILITY = 1.256_637_061_27E-6;

    public static Vector2 coulombLawField(Vector2 sourcePos, float charge, Vector2 testPoint)
    {
        Vector2 diffVector = testPoint.sub(sourcePos);
        double magnitude = COULOMB_CONSTANT * charge / diffVector.lengthSq();
        return diffVector.normalize().mult((float)magnitude);
    }

    public static Vector3 ampereCircuitalLaw(Vector2 wirePoint, Vector2 current, Vector2 testPoint)
    {
        Vector2 diffVector = testPoint.sub(closestPointOnLine(wirePoint, current, testPoint));
        Vector3 dir = new Vector3(current).cross(new Vector3(diffVector)).normalize();
        double magnitude = VACUUM_PERMEABILITY * current.length() / (2 * Math.PI * diffVector.length());

        return dir.mult((float)magnitude);
    }

    public static Vector2 closestPointOnLine(Vector2 linePoint, Vector2 lineDirection, Vector2 testPoint)
    {
        // find intersection point of the line and a line perpendicular to it that passes through testPoint
        // direction of that line will be Vector2(-lineDirection.y, lineDirection.x) (rotated 90 degrees)

        // Line: y = line.y + (dir.y / dir.x) * (x - line.x)
        // Perpendicular: y = point.y + (- dir.x / dir.y) * (x - point.x)

        // line.y + (dir.y / dir.x) * (x - line.x) = point.y + (- dir.x / dir.y) * (x - point.x)
        // (dir.y / dir.x) * (x - line.x) - (- dir.x / dir.y) * (x - point.x) = point.y - line.y
        // (dir.y / dir.x + dir.x / dir.y) * x = point.y - line.y - ((dir.y / dir.x) * -line.x) + ((-dir.x / dir.y) * -point.x)
        // (dir.y / dir.x + dir.x / dir.y) * x = point.y - line.y + (dir.y / dir.x) * line.x + (dir.x / dir.y) * point.x
        // x = (point.y - line.y + (dir.y / dir.x) * line.x + (dir.x / dir.y) * point.x) / (dir.y / dir.x + dir.x / dir.y)

        // edge cases where line is horizontal, vertical, or if direction vector is 0
        if (lineDirection.equals(Vector2.zero()))
            throw new IllegalArgumentException("Line direction cannot be the zero vector");

        if (lineDirection.x() == 0)
            return new Vector2(linePoint.x(), testPoint.y());

        if (lineDirection.y() == 0)
            return new Vector2(testPoint.x(), linePoint.y());
        
        double lineSlope = lineDirection.y() / lineDirection.x();
        double invSlope = 1 / lineSlope;

        double x = (testPoint.y() - linePoint.y() + lineSlope * linePoint.x() + invSlope * testPoint.x()) / (lineSlope + invSlope);
        double y = linePoint.y() + lineSlope * (x - linePoint.x());

        return new Vector2((float)x, (float)y);
    }
}