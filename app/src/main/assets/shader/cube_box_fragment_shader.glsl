precision mediump float;
varying vec2 vUV;
uniform samplerCube uTexture;
void main(){
    gl_FragColor = textureCube(u_TextureUnit, vUV);
}

