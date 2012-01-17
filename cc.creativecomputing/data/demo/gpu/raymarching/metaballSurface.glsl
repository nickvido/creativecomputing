uniform float time;

float surfacefunction(vec3 hitpoint) {
    //iso surface metaballs
    float size = 0.175;
    vec3 ball0 = vec3(cos(time),1.0,0.0) - hitpoint;
    vec3 ball1 = vec3(-1.0,-sin(time),0.0) - hitpoint;
    vec3 ball2 = vec3(-cos(time),-sin(time),1.0) - hitpoint;
    vec3 ball3 = vec3(-cos(time),sin(time),2.0*sin(time)) - hitpoint;
    vec3 ball4 = vec3(-2.0 * cos(time), -sin(time),2.0*sin(time))-hitpoint;
    vec3 ball5 = vec3(-0.5 * cos(time), -cos(time),2.5*sin(time))-hitpoint;
    vec3 ball6 = vec3(-0.5 * cos(time), -1.5 * cos(time), 1.75 * sin(time)) - hitpoint;
    
    float ball0dist = dot(ball0, ball0);
    float ball1dist = dot(ball1, ball1);
    float ball2dist = dot(ball2, ball2);
    float ball3dist = dot(ball3, ball3);
    float ball4dist = dot(ball4, ball4);
    float ball5dist = dot(ball5, ball5);
    float ball6dist = dot(ball6, ball6);
    
    ball0dist = pow(ball0dist, max(0.02, 1.0));
    ball1dist = pow(ball1dist, max(0.02, 1.0));
    ball2dist = pow(ball2dist, max(0.02, 1.0));
    ball3dist = pow(ball3dist, max(0.02, 1.0));
    ball4dist = pow(ball4dist, max(0.02, 1.0));
    ball5dist = pow(ball5dist, max(0.02, 1.0));
    ball6dist = pow(ball6dist, max(0.02, 1.0));
    return -(
    	size / ball0dist +
    	size / ball1dist +
    	size / ball2dist +
    	size / ball3dist +
    	size / ball4dist +
    	size / ball5dist +
    	size / ball6dist
    ) + 1.0;
}

