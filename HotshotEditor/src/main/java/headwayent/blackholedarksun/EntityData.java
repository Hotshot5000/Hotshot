package headwayent.blackholedarksun;

import headwayent.hotshotengine.ENG_Vector3D;

public class EntityData {

    public String inGameName;
    public String filename;
    public String name;
    public float maxSpeed;
    public float turnAngle;
    public int acceleration = 5; // percentage
    public float maxAngularVelocity;
    public float weight;
    public float linearDamping = 0.1f;
    public float angularDamping = 0.1f;
    public int health = 1;
    public ENG_Vector3D localInertia = new ENG_Vector3D();

    public EntityData() {
        super();
    }

}