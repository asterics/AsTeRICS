function [dataTrain,dataTest]=openFiles(dirPath,filePrefix)
if nargin<1
    dirPath='Z:\UCONTROL-P20070527-01\BCI competition iii\OVR_aproach\features subject k3b\';
end
if nargin<2
    filePrefix='';
end

for i=1:4
    disp(i)
    filename=[dirPath,filePrefix,'features4aproach1_c',int2str(i)]
    load(filename)
    dataTrain(i)=data
    filename=[dirPath,filePrefix,'test_features4aproach1_c',int2str(i)]
    load(filename)
    dataTest(i)=data
end