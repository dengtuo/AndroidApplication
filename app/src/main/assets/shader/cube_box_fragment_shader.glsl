precision mediump float;
varying vec3 v_Position;
uniform samplerCube uTexture;
void main(){
    gl_FragColor = textureCube(uTexture, v_Position);
}

