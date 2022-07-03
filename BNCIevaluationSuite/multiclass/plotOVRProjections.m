%[dataTrain,dataTest]=openFiles()

c1=dataTrain(1,1);
c2=dataTrain(1,2);
c3=dataTrain(1,3);
c4=dataTrain(1,4);
minNumberChannels=min([size(c1.features,1),size(c2.features,1),size(c3.features,1),size(c4.features,1)])
for chanel=1:minNumberChannels

figure
a=squeeze(c4.features(chanel,:,c4.GT==1));
plot(a(:))
hold all
a=squeeze(c4.features(chanel,:,c4.GT==0));
plot(a(:))
title(['chan ',int2str(chanel),' projection training on class 4 space'])

figure
a=squeeze(c3.features(chanel,:,c3.GT==1));
plot(a(:))
hold all
a=squeeze(c3.features(chanel,:,c3.GT==0));
plot(a(:))
title(['chan ',int2str(chanel),' projection training on class 3 space'])

figure
a=squeeze(c2.features(chanel,:,c2.GT==1));
plot(a(:))
hold all
a=squeeze(c2.features(chanel,:,c2.GT==0));
plot(a(:))
title(['chan ',int2str(chanel),' projection training on class 2 space'])

figure
a=squeeze(c1.features(chanel,:,c1.GT==1));
plot(a(:))
hold all
a=squeeze(c1.features(chanel,:,c1.GT==0));
plot(a(:))
title(['chan ',int2str(chanel),' projection training on class 1 space'])

figure
a=squeeze(c1.features(chanel,:,c1.GT==1));
plot(a(:))
hold all
a=squeeze(c1.features(chanel,:,c2.GT==1));
plot(a(:))
a=squeeze(c1.features(chanel,:,c3.GT==1));
plot(a(:))
a=squeeze(c1.features(chanel,:,c4.GT==1));
plot(a(:))
title(['chan ',int2str(chanel),' projection training on class 1 space'])

c1=dataTest(1,1);
c2=dataTest(1,2);
c3=dataTest(1,3);
c4=dataTest(1,4);

figure
a=squeeze(c1.features(chanel,:,c1.GT==1));
plot(a(:))
hold all
a=squeeze(c1.features(chanel,:,c2.GT==1));
plot(a(:))
a=squeeze(c1.features(chanel,:,c3.GT==1));
plot(a(:))
a=squeeze(c1.features(chanel,:,c4.GT==1));
plot(a(:))
title(['chan ',int2str(chanel),' projection test on class 1 space'])

figure
a=squeeze(c1.features(chanel,:,c1.GT==1));
plot(a(:))
hold all
a=squeeze(c1.features(chanel,:,c1.GT==0));
plot(a(:))
title(['chan ',int2str(chanel),' projection test on class 1 space'])

figure
a=squeeze(c2.features(chanel,:,c2.GT==1));
plot(a(:))
hold all
a=squeeze(c2.features(chanel,:,c2.GT==0));
plot(a(:))
title(['chan ',int2str(chanel),' projection test on class 2 space'])

figure
a=squeeze(c3.features(chanel,:,c3.GT==1));
plot(a(:))
hold all
a=squeeze(c3.features(chanel,:,c3.GT==0));
plot(a(:))
title(['chan ',int2str(chanel),' projection test on class 3 space'])

figure
a=squeeze(c4.features(chanel,:,c4.GT==1));
plot(a(:))
hold all
a=squeeze(c4.features(chanel,:,c4.GT==0));
plot(a(:))
title(['chan ',int2str(chanel),' projection test on class 4 space'])

figure
a=squeeze(c4.features(chanel,:,c4.GT==1));
plot(a(:))
hold all
a=squeeze(c4.features(chanel,:,c1.GT==1));
plot(a(:))
a=squeeze(c4.features(chanel,:,c2.GT==1));
plot(a(:))
a=squeeze(c4.features(chanel,:,c3.GT==1));
plot(a(:))
title(['chan ',int2str(chanel),' projection test on class 4 space'])

end
