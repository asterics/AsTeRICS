function decimatedData=starDecimate(data,ratio)
for i=1:size(data,1)
    decimatedData(i,:)=decimate(data(i,:),ratio);
end
