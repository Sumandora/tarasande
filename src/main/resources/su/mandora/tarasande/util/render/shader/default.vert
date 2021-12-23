varying vec4 vertColor;

void main(){
    gl_Position = gl_ModelViewProjectionMatrix*gl_Vertex;
    gl_TexCoord[0] = gl_MultiTexCoord0;
    vertColor = vec4(1.0, 1.0, 1.0, 1.0);
}
