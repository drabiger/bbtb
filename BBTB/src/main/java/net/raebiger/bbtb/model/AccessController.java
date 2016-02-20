package net.raebiger.bbtb.model;

import java.util.List;

public interface AccessController<T> {

	public T getByUuid(String uuid);

	public List<T> getAll();

	public void persist(T object);

	public void delete(T object);

	public List<T> filterForReadAccess(List<T> allObjects);

	boolean isReadable(T object);

	boolean mayUserDelete(T object);

	boolean mayUserModify(T object);
}
